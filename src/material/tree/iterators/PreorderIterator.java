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

    private final Queue<Position<E>> nodeQueue;
    private final Tree<E> tree;

    //Constructores

    public PreorderIterator(Tree<E> tree) {
        this.nodeQueue = new ArrayDeque<>();
        this.tree = tree;
        if (!this.tree.isEmpty()) {
            recorrido(this.tree.root(), null);
        }
    }

    public PreorderIterator(Tree<E> tree, Position<E> start) {
        this.nodeQueue = new ArrayDeque<>();
        this.tree = tree;
        recorrido(start, null);
}

    public PreorderIterator(Tree<E> tree, Position<E> start, Predicate<Position<E>> predicate) {
        this.nodeQueue = new ArrayDeque<>();
        this.tree = tree;
        recorrido(start, predicate);
    }


    @Override
    public boolean hasNext() {
        return !this.nodeQueue.isEmpty();
    }

    //simplemente se recupera el primero de la cola con el recorrido ya completo
    @Override
    public Position<E> next() {

        if(this.nodeQueue.isEmpty()){
            throw new NoSuchElementException();
        }

        return this.nodeQueue.poll();
    }

    //Logica del iterador (se recorre desde la posicion start hasta el final almacenando el recorrido en una cola)
    private void recorrido(Position<E> start, Predicate<Position<E>> predicate){
        if(start==null)
            throw new RuntimeException("Position null");

        Stack<Position<E>> nodeStack = new Stack<>();
        Stack<Position<E>> nodeStackAux = new Stack<>();
        Position<E> aux;

        nodeStack.push(start);

        while(!nodeStack.empty()){
            aux = nodeStack.pop();
            for (Position<E> node : tree.children(aux)) {
                nodeStackAux.push(node);
            }
            while(!nodeStackAux.empty()){
                Position<E> elem = nodeStackAux.pop();
                nodeStack.push(elem);
            }
            if((predicate==null) || (predicate.test(aux)))
                this.nodeQueue.add(aux);
        }
    }
}

