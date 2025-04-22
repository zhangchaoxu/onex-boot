package com.nb6868.onex.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.map.MapUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 树结构工具
 * 注意id不能重复
 * 比如以下就会以为子节点id相同出现部分重复数据被吞掉的问题
 * -价格法
 * --第一条
 * --第二条
 * -广告法
 * --第一条
 * --第二条
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class TreeNodeUtils {

    // 根节点
    public final static Long ROOT_ID = 0L;
    public final static String ROOT_CODE = "0";

    /**
     * ID树属性
     */
    public static TreeNodeConfig getIdTreeNodeConfig() {
        return new TreeNodeConfig()
                .setIdKey("id")
                .setParentIdKey("pid")
                .setWeightKey("sort");
    }

    /**
     * CODE树属性
     */
    public static TreeNodeConfig getCodeTreeNodeConfig() {
        return new TreeNodeConfig()
                .setIdKey("code")
                .setParentIdKey("pcode")
                .setWeightKey("sort");
    }

    /**
     * 默认的Node解析器
     */
    public static <E> NodeParser<cn.hutool.core.lang.tree.TreeNode<E>, E> defaultNodeParser() {
        return (treeNode, tree) -> {
            tree.setId(treeNode.getId()).setParentId(treeNode.getParentId()).setWeight(treeNode.getWeight()).setName(treeNode.getName());
            MapUtil.emptyIfNull(treeNode.getExtra()).forEach(tree::putExtra);
        };
    }

    /**
     * 构建CODE格式的树结构
     */
    public static List<Tree<String>> buildCodeTree(List<TreeNode<String>> list) {
        return buildCodeTree(list, ROOT_CODE);
    }

    /**
     * 构建CODE格式的树结构
     */
    public static List<Tree<String>> buildCodeTree(List<TreeNode<String>> list, String rootNode) {
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        return TreeUtil.build(CollUtil.emptyIfNull(list), rootNode, getCodeTreeNodeConfig(), defaultNodeParser());
    }

    /**
     * 构建CODE格式的树结构,自动计算RootId
     * rootId计算办法：使用集合获取id与parentId的交集，再从parentId中去除交集，剩下的作为rootId
     * 每个rootId分别构建，最后添加到同一个集合中。
     * <a href="https://github.com/chinabugotech/hutool/issues/3856">参考</a>
     */
    public static List<Tree<String>> buildCodeTreeAutoRoot(List<TreeNode<String>> list) {
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        // 从list中提取id和pid的set
        Set<String> idList = new HashSet<>();
        Set<String> pidList = new HashSet<>();
        list.forEach(treeNode -> {
            if (null != treeNode.getId()) {
                idList.add(treeNode.getId());
            }
            if (null != treeNode.getParentId()) {
                pidList.add(treeNode.getParentId());
            }
        });
        // 去重后的交集
        Set<String> distinctIds = CollUtil.intersectionDistinct(idList, pidList);
        // 从parentId中去除交集,剩下的应该是rootId
        List<String> subtractIds = CollUtil.subtractToList(pidList, distinctIds);
        if (CollUtil.isEmpty(subtractIds)) {
            // 没有根节点
            return CollUtil.newArrayList();
        } else if (subtractIds.size() == 1) {
            // 只有1个根节点
            return buildCodeTree(list, subtractIds.get(0));
        } else {
            // 存在多个根节点,根节点数据有吗?
            List<Tree<String>> resultList = new ArrayList<>();
            subtractIds.forEach(aLong -> {
                // 构建树结构
                resultList.addAll(buildCodeTree(list, aLong));
            });
            return resultList;
        }
    }

    /**
     * 构建ID格式的树结构
     */
    public static List<Tree<Long>> buildIdTree(List<TreeNode<Long>> list) {
        return buildIdTree(list, ROOT_ID);
    }

    /**
     * 构建ID格式的树结构
     */
    public static List<Tree<Long>> buildIdTree(List<TreeNode<Long>> list, Long rootNode) {
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        return TreeUtil.build(CollUtil.emptyIfNull(list), rootNode, getIdTreeNodeConfig(), defaultNodeParser());
    }

    /**
     * 构建ID格式的树结构,自动计算RootId
     * rootId计算办法：使用集合获取id与parentId的交集，再从parentId中去除交集，剩下的作为rootId
     * 每个rootId分别构建，最后添加到同一个集合中。
     * <a href="https://github.com/chinabugotech/hutool/issues/3856">参考</a>
     */
    public static List<Tree<Long>> buildIdTreeAutoRoot(List<TreeNode<Long>> list) {
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        // 从list中提取id和pid的set
        Set<Long> idList = new HashSet<>();
        Set<Long> pidList = new HashSet<>();
        list.forEach(treeNode -> {
            if (null != treeNode.getId()) {
                idList.add(treeNode.getId());
            }
            if (null != treeNode.getParentId()) {
                pidList.add(treeNode.getParentId());
            }
        });
        // 去重后的交集
        Set<Long> distinctIds = CollUtil.intersectionDistinct(idList, pidList);
        // 从parentId中去除交集,剩下的应该是rootId
        List<Long> subtractIds = CollUtil.subtractToList(pidList, distinctIds);
        if (CollUtil.isEmpty(subtractIds)) {
            // 没有根节点
            return CollUtil.newArrayList();
        } else if (subtractIds.size() == 1) {
            // 只有1个根节点
            return buildIdTree(list, subtractIds.get(0));
        } else {
            // 存在多个根节点,根节点数据有吗?
            List<Tree<Long>> resultList = new ArrayList<>();
            subtractIds.forEach(aLong -> {
                // 构建树结构
                resultList.addAll(buildIdTree(list, aLong));
            });
            return resultList;
        }
    }

}
