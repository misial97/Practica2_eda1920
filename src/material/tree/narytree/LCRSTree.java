package material.tree.narytree;

import material.Position;
import material.tree.iterators.BFSIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A linked class for a tree where nodes have an arbitrary number of children.
 *
 * @param <E> the elements stored in the tree
 * @author Raul Cabido, Abraham Duarte, Jose Velez, Jesús Sánchez-Oro
 */
public class LCRSTree<E> implements NAryTree<E> {

    /**
     * Inner class which represents a node of the tree
     *
     * @param <T> the type of the elements stored in a node
     */
    private class TreeNode<T> implements Position<T> {

        private T element; // The element stored in the position
        private TreeNode<T> parent; // The parent of the node
        private TreeNode<T> child; // The first child of the node
        private TreeNode<T> sibling;// The next sibling of the node
        private LCRSTree<T> myTree; // A reference to the tree where the node belongs

        public TreeNode(T element, TreeNode<T> parent, TreeNode<T> child, TreeNode<T> sibling, LCRSTree<T> myTree) {
            this.element = element;
            this.parent = parent;
            this.child = child;
            this.sibling = sibling;
            this.myTree = myTree;
        }

        @Override
        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }

        public TreeNode<T> getParent() {
            return parent;
        }

        public void setParent(TreeNode<T> parent) {
            this.parent = parent;
        }

        public TreeNode<T> getChild() {
            return child;
        }

        public void setChild(TreeNode<T> child) {
            this.child = child;
        }

        public TreeNode<T> getSibling() {
            return sibling;
        }

        public void setSibling(TreeNode<T> sibling) {
            this.sibling = sibling;
        }

        public LCRSTree<T> getMyTree() {
            return myTree;
        }

        public void setMyTree(LCRSTree<T> myTree) {
            this.myTree = myTree;
        }
    }

    private TreeNode<E> root;
    private int size;

    public LCRSTree() {
        root = null;
        size = 0;
    }
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {  return this.size == 0; }

    @Override
    public Position<E> root() throws RuntimeException {
        if (this.root == null) {
            throw new RuntimeException("The tree is empty");
        }
        return this.root;
    }

    @Override
    public Position<E> parent(Position<E> v) throws RuntimeException {
        TreeNode<E> node = checkPosition(v);
        Position<E> parentPos = node.getParent();
        if (parentPos == null) {
            throw new RuntimeException("The node has not parent");
        }
        return parentPos;
    }

    @Override
    public Iterable<? extends Position<E>> children(Position<E> v) {
        TreeNode<E> node = checkPosition(v);
        List<TreeNode<E>> children = new ArrayList<>();
        TreeNode<E> childAux = node.getChild();
        if(childAux!=null) {
            children.add(childAux);

            while (childAux.getSibling() != null) {
                childAux = childAux.getSibling();
                children.add(childAux);
            }
        }

        return children;
    }

    @Override
    public boolean isInternal(Position<E> v) {  return !isLeaf(v);  }

    @Override
    public boolean isLeaf(Position<E> v) throws RuntimeException {
        TreeNode<E> node = checkPosition(v);
        return node.getChild() == null;
    }

    @Override
    public boolean isRoot(Position<E> v) {
        TreeNode<E> node = checkPosition(v);
        return (node == this.root());
    }

    @Override
    public Position<E> addRoot(E e) throws RuntimeException {
        if (!isEmpty()) {
            throw new IllegalStateException("Tree already has a root");
        }
        this.size = 1;
        this.root = new TreeNode<>(e, null, null, null, this);
        return root;
    }

    @Override
    public Iterator<Position<E>> iterator() {
        return new BFSIterator<>(this);
    }

    @Override
    public E replace(Position<E> p, E e) {
        TreeNode<E> node = checkPosition(p);
        E temp = p.getElement();
        node.setElement(e);
        return temp;
    }

    @Override
    public void swapElements(Position<E> p1, Position<E> p2) {
        TreeNode<E> node1 = checkPosition(p1);
        TreeNode<E> node2 = checkPosition(p2);
        E temp = p2.getElement();
        node2.setElement(p1.getElement());
        node1.setElement(temp);
    }

    @Override
    public Position<E> add(E element, Position<E> p) {
        TreeNode<E> parent = checkPosition(p);
        TreeNode<E> newNode = new TreeNode<>(element, parent, null, null, this);
        if(parent.getChild()==null){
            parent.setChild(newNode);
        }else{
            TreeNode<E> childAux = parent.getChild();
            while (childAux.getSibling()!=null){
                childAux = childAux.getSibling();
            }
            childAux.setSibling(newNode);
        }
        this.size++;
        return newNode;
    }

    @Override
    public void remove(Position<E> p) {
        TreeNode<E> node = checkPosition(p);
        if (node.getParent() != null) {
            Iterator<Position<E>> it = new BFSIterator<E>(this, p);
            int cont = 0;
            while (it.hasNext()) {
                TreeNode<E> next = checkPosition(it.next());
                next.setMyTree(null);
                cont++;
            }
            size = size - cont;
            TreeNode<E> parent = node.getParent();
            parent.setChild(null);
        } else {
            this.root = null;
            this.size = 0;
        }
        node.setMyTree(null);
    }

    @Override
    public void moveSubtree(Position<E> pOrig, Position<E> pDest) throws RuntimeException {
        TreeNode<E> origen = checkPosition(pOrig);
        TreeNode<E> destino = checkPosition(pDest);

        if(origen.equals(this.root)){
            throw new RuntimeException("Root node can't be moved");
        }else if(pOrig.equals(pDest)){
            throw new RuntimeException("Both positions are the same");
        }
        //comprobacion de si el nodo destino es hijo del nodo origen
        Iterator<Position<E>> it = new BFSIterator<>(this,pOrig);
        while(it.hasNext()){
            Position<E> p = it.next();
            if(p==pDest){
                throw new RuntimeException("Target position can't be a sub tree of origin");
            }
        }

        origen.getParent().setChild(null);//borras hijo destino de su padre

        if(destino.getChild()==null){//aniades hijo al padre destino
            destino.setChild(origen);
        }else{
            TreeNode<E> childAux = destino.getChild();
            while (childAux.getSibling()!=null){
                childAux = childAux.getSibling();
            }
            childAux.setSibling(origen);
        }

        origen.setParent(destino); //cambias el padre a la raiz del subarbol a mover

    }

    private TreeNode<E> checkPosition(Position<E> p)
            throws IllegalStateException {
        if (p == null || !(p instanceof TreeNode)) {
            throw new IllegalStateException("The position is invalid");
        }
        TreeNode<E> aux = (TreeNode<E>) p;

        if (aux.getMyTree() != this) {
            throw new IllegalStateException("The node is not from this tree");
        }
        return aux;
    }
}
