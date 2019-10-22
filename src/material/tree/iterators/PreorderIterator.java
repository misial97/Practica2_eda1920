package material.tree.iterators;

import material.Position;
import material.tree.Tree;

import java.util.*;
import java.util.function.Predicate;

/**
 * Generic preorder iterator for trees.
 *
 * @param <E>
 * @author A. Duarte, J. Vélez, J. Sánchez-Oro, JD. Quintana
 */
//TODO: Practica 2 Ejercicio 3
public class PreorderIterator<E> implements Iterator<Position<E>> {

    private final Stack<Position<E>> nodeStack;
    private final Stack<Position<E>> nodeStackAux;
    private final Tree<E> tree;

    public PreorderIterator(Tree<E> tree) {
        this.nodeStack = new Stack<>();
        this.nodeStackAux = new Stack<>();
        this.tree = tree;
        if (!this.tree.isEmpty()) {
            this.nodeStack.add(tree.root());
        }
    }

    public PreorderIterator(Tree<E> tree, Position<E> start) {
        this.nodeStack = new Stack<>();
        this.nodeStackAux = new Stack<>();
        this.tree = tree;
        this.nodeStack.add(start);
}

    public PreorderIterator(Tree<E> tree, Position<E> start, Predicate<Position<E>> predicate) {
        this.nodeStack = new Stack<>();
        this.nodeStackAux = new Stack<>();
        this.tree = tree;
        this.nodeStack.add(start);
    }


    @Override
    public boolean hasNext() {
        return !this.nodeStack.empty();
    }

    @Override
    public Position<E> next() {

        if(this.nodeStack.empty()){
            throw new NoSuchElementException();
        }
        Position<E> aux = this.nodeStack.pop();
        for (Position<E> node : tree.children(aux)) {
            this.nodeStackAux.push(node);
        }
        while(!this.nodeStackAux.empty()){
            Position<E> elem = this.nodeStackAux.pop();
            this.nodeStack.push(elem);
        }
        return aux;
    }

}
