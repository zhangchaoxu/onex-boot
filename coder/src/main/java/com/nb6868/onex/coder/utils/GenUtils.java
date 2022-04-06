package com.nb6868.onex.coder.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.Entity;
import com.nb6868.onex.coder.entity.CodeGenerateConfig;
import com.nb6868.onex.coder.entity.ColumnEntity;
import com.nb6868.onex.coder.entity.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public class GenUtils {

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("template/DTO.java.vm");
        templates.add("template/Entity.java.vm");
        templates.add("template/Dao.java.vm");
        /*templates.add("template/Dao.xml.vm");*/
        templates.add("template/Service.java.vm");
        /*templates.add("template/ServiceImpl.java.vm");*/
        templates.add("template/Controller.java.vm");
        templates.add("template/Excel.java.vm");
        templates.add("template/index.vue.vm");
        templates.add("template/add-or-update.vue.vm");
        templates.add("template/mysql.vm");
        return templates;
    }

    /**
     * 生成代码
     *
     * @param table              生成的表
     * @param columns            生成的字段
     * @param codeGenerateConfig 生成配置
     * @param zip                输出的压缩包
     */
    public static void generatorCode(Map<String, Object> table,
                                     List<Entity> columns,
                                     CodeGenerateConfig codeGenerateConfig,
                                     ZipOutputStream zip) {
        //配置信息
        Configuration config = getConfig();
        boolean hasBigDecimal = false;
        boolean hasJson = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(MapUtil.getStr(table, "table_name"));
        tableEntity.setComments(MapUtil.getStr(table, "table_comment"));
        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), codeGenerateConfig.getTablePrefix());
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columnsList = new ArrayList<>();
        for (Entity column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(MapUtil.getStr(column, "column_name"));
            columnEntity.setDataType(MapUtil.getStr(column, "data_type"));
            columnEntity.setComments(MapUtil.getStr(column, "column_comment"));
            columnEntity.setExtra(MapUtil.getStr(column, "extra"));

            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            if (!hasJson && "JSONObject".equals(attrType)) {
                hasJson = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(MapUtil.getStr(column, "column_key")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columnsList.add(columnEntity);
        }
        tableEntity.setColumns(columnsList);

        // 没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        // 设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname());
        if (StringUtils.isNotBlank(codeGenerateConfig.getTablePrefix())) {
            map.put("pathNameDash", tableEntity.getTableName().replaceFirst(codeGenerateConfig.getTablePrefix() + "_", "").replace("_", "-"));
        } else {
            map.put("pathNameDash", tableEntity.getTableName().replace("_", "-"));
        }
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("hasJson", hasJson);
        map.put("version", codeGenerateConfig.getVersion());
        map.put("package", codeGenerateConfig.getPackageName());
        map.put("moduleName", codeGenerateConfig.getModuleName());
        map.put("author", codeGenerateConfig.getAuthorName());
        map.put("email", codeGenerateConfig.getAuthorEmail());
        map.put("datetime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        for (int i = 0; i <= 10; i++) {
            map.put("id" + i, IdUtil.getSnowflake(1,  1).nextId());
        }

        VelocityContext context = new VelocityContext(map);
        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(getFileName(template, tableEntity.getClassName(), codeGenerateConfig.getPackageName(), codeGenerateConfig.getModuleName(), (String) map.get("pathNameDash"))));
                IoUtil.write(zip, false, sw.toString().getBytes());
                sw.close();
                zip.closeEntry();
            } catch (IOException e) {
                throw new OnexException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new OnexException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName, String pathNameDash) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + "modules" + File.separator + moduleName + File.separator;
        }

        if (template.contains("Entity.java.vm")) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }

        if (template.contains("Excel.java.vm")) {
            return packagePath + "excel" + File.separator + className + "Excel.java";
        }

        if (template.contains("Dao.java.vm")) {
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

		/*if (template.contains("ServiceImpl.java.vm" )) {
			return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
		}*/

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

		/*if (template.contains("Dao.xml.vm" )) {
			return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + moduleName + File.separator + className + "Dao.xml";
		}*/

        if (template.contains("DTO.java.vm")) {
            return packagePath + "dto" + File.separator + className + "DTO.java";
        }

        if (template.contains("index.vue.vm")) {
            return "vue" + File.separator + "views" + File.separator + "modules" +
                    File.separator + moduleName + File.separator + pathNameDash + ".vue";
        }

        if (template.contains("add-or-update.vue.vm")) {
            return "vue" + File.separator + "views" + File.separator + "modules" +
                    File.separator + moduleName + File.separator + pathNameDash + "-add-or-update.vue";
        }

        if (template.contains("mysql.vm")) {
            return className.toLowerCase() + ".mysql.sql";
        }

		/*if (template.contains("oracle.vm" )) {
			return className.toLowerCase() + ".oracle.sql";
		}

		if (template.contains("sqlserver.vm" )) {
			return className.toLowerCase() + ".sqlserver.sql";
		}

		if (template.contains("postgresql.vm" )) {
			return className.toLowerCase() + ".postgresql.sql";
		}*/

        return null;
    }
}
