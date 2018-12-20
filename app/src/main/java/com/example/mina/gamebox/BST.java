package com.example.mina.gamebox;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;

public class BST{

    private ArrayList<Integer> tree;

    Node root;

    public Node getRoot() {
        return root;
    }

    public BST()
    {
        root = null;
        tree = new ArrayList<Integer>();
    }

    public void insert(int value)
    {
       if(find(value) != null) return;
       tree.add(value);
       root = insertKey(root, value);
    }

    private Node insertKey(Node start, int key)
    {
        if(start == null)
        {
            start = new Node(key);
            return start;
        }

        if(key < start.value)
        {
            start.left = insertKey(start.left, key);
        }
        else
        {
            start.right = insertKey(start.right, key);
        }

        return start;
    }

    public void delete(int key)
    {
        if(root == null) return; //empty tree

        Node parent = null, node = root;

        while(node != null || node.value == key)
        {
            parent = node;
            if(key < node.value)
            {
                node = node.left;
            }
            else
            {
                node = node.right;
            }
        }

        if(node == null) return; //value doesn't exist

        if(node.right == null && node.left == null) //leaf node
        {
            if(parent.value < node.value)
                parent.right = null;
            else
                parent.left = null;
        }
        else if(node.right != null && node.left == null) //one right child
        {
            if(parent.value < node.value)
                parent.right = node.right;
            else
                parent.left = node.right;
        }
        else if(node.right == null && node.left != null) //one left child
        {
            if(parent.value < node.value)
                parent.right = node.left;
            else
                parent.left = node.left;
        }
        else
        {
            Node minNode = findMin(node.right);
            parent = findParent(minNode.value);

            node.value = minNode.value;

            if(parent == node) //if the parent of the minimum node is the current node
            {
                parent.right = minNode.right; //min node doesn't have any left children, shift up right tree
            }
            else
            {
                parent.left = minNode.right;
            }
        }
    }

    private Node findParent(int value)
    {
        Node parent = null, node = root;
        while(node != null)
        {
            if(node.value == value) break;
            parent = node;
            node = node.left;
        }
        return parent;
    }

    private Node findMin(Node node)
    {
        while(node.left != null)
        {
            node = node.left;
        }
        return node;
    }

    private Node find(int value)
    {
        Node node = root;
        while(node != null && node.value != value)
        {
            if (value < node.value)
                node = node.left;
            else
                node = node.right;
        }
        return node;
    }


    private int setNodePosition(boolean isRight , int level , Node currNode , int prevHPos , int pivot){
        if(currNode == null) return 0;

        if(isRight){

            int re = setNodePosition(isRight , level + 1 ,  currNode.left , prevHPos - 1 , pivot);

            prevHPos += re;

            if(prevHPos <= pivot){
                re = pivot - prevHPos + 1;
                prevHPos = pivot + 1;
            }

            setNodePosition(isRight , level + 1 , currNode.right , prevHPos + 1 , prevHPos);

            currNode.setHOrder(prevHPos);
            currNode.setVOrder(level);

            return re;
        }
        else{

            int re = setNodePosition(isRight , level + 1 , currNode.right , prevHPos + 1 , pivot);

            prevHPos += re;

            if(prevHPos >= pivot){
                re = pivot - prevHPos - 1;
                prevHPos = pivot - 1;
            }

            setNodePosition(isRight , level + 1 ,  currNode.left , prevHPos - 1 , prevHPos);

            currNode.setHOrder(prevHPos);
            currNode.setVOrder(level);

            return re;
        }
    }

    public void setTree(){
        if(root != null){
            root.setVOrder(0);
            root.setHOrder(0);
            setNodePosition(true , 1 , root.right , 1 , 0);
            setNodePosition(false , 1 , root.left , -1 , 0);
        }
    }

    private Node makeBalanced(int l , int r){
        if(l > r) return null;

        int mid = (l + r) / 2;
        Node node = new Node(tree.get(mid));
        node.left = makeBalanced(l , mid - 1);
        node.right = makeBalanced(mid + 1 , r);

        return  node;
    }

    public void balance()
    {
        Collections.sort(tree);

        root = makeBalanced(0 , tree.size() - 1);
    }
}
