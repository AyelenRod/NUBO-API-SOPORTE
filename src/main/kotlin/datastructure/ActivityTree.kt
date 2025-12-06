package com.soporte.datastructures

class ActivityBST {
    data class ActivityTreeNode(
        val activityId: String,
        val difficulty: Int,
        val title: String,
        var left: ActivityTreeNode? = null,
        var right: ActivityTreeNode? = null
    )

    private var root: ActivityTreeNode? = null

    fun insert(activityId: String, difficulty: Int, title: String) {
        root = insertRec(root, activityId, difficulty, title)
    }

    private fun insertRec(
        node: ActivityTreeNode?,
        activityId: String,
        difficulty: Int,
        title: String
    ): ActivityTreeNode {
        if (node == null) {
            return ActivityTreeNode(activityId, difficulty, title)
        }

        when {
            difficulty < node.difficulty -> node.left = insertRec(node.left, activityId, difficulty, title)
            difficulty > node.difficulty -> node.right = insertRec(node.right, activityId, difficulty, title)
            else -> {
                node.right = insertRec(node.right, activityId, difficulty, title)
            }
        }

        return node
    }

    fun findActivitiesByDifficultyRange(minDiff: Int, maxDiff: Int): List<ActivityTreeNode> {
        val result = mutableListOf<ActivityTreeNode>()
        findInRange(root, minDiff, maxDiff, result)
        return result
    }

    private fun findInRange(
        node: ActivityTreeNode?,
        minDiff: Int,
        maxDiff: Int,
        result: MutableList<ActivityTreeNode>
    ) {
        if (node == null) return

        if (node.difficulty > minDiff) {
            findInRange(node.left, minDiff, maxDiff, result)
        }

        if (node.difficulty in minDiff..maxDiff) {
            result.add(node)
        }

        if (node.difficulty < maxDiff) {
            findInRange(node.right, minDiff, maxDiff, result)
        }
    }

    fun inOrderTraversal(): List<ActivityTreeNode> {
        val result = mutableListOf<ActivityTreeNode>()
        inOrderRec(root, result)
        return result
    }

    private fun inOrderRec(node: ActivityTreeNode?, result: MutableList<ActivityTreeNode>) {
        if (node == null) return
        inOrderRec(node.left, result)
        result.add(node)
        inOrderRec(node.right, result)
    }

    fun clear() {
        root = null
    }

    fun isEmpty() = root == null
}