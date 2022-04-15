package com.nb6868.onex.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.map.MapUtil;

import java.util.List;

/**
 * 树结构工具
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
        return TreeUtil.build(CollUtil.emptyIfNull(list), ROOT_CODE, getCodeTreeNodeConfig(), defaultNodeParser());
    }

    /**
     * 构建编码格式的树结构
     */
    public static List<Tree<Long>> buildIdTree(List<TreeNode<Long>> list) {
        return TreeUtil.build(CollUtil.emptyIfNull(list), ROOT_ID, getIdTreeNodeConfig(), defaultNodeParser());
    }

}
