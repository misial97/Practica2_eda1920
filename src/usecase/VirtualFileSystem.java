package usecase;


import material.Position;
import material.tree.iterators.PreorderIterator;
import material.tree.narytree.LinkedTree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VirtualFileSystem {

    //TODO: Ejercicio 4 Caso de uso
    private LinkedTree<File> tree;
    private List<Position<File>> nodeList;

    public VirtualFileSystem(){
        this.tree = new LinkedTree<>();
        this.nodeList = new ArrayList<>();

    }

    public void loadFileSystem(String path) {
        File root = new File(path);
        if(!root.exists()){
            throw new RuntimeException("Path does not exists!");
        }
        if(this.tree.isEmpty()) {
            Position<File> positionRoot = this.tree.addRoot(root);
            this.nodeList.add(positionRoot);
            loadFileSystemAux(root.listFiles(), positionRoot);

        }else{
            this.tree = new LinkedTree<>();
            this.nodeList = new ArrayList<>();
            Position<File> positionRoot = this.tree.addRoot(root);
            this.nodeList.add(positionRoot);
            loadFileSystemAux(root.listFiles(), positionRoot);
        }
    }

    private void loadFileSystemAux(File[] nodes, Position<File> parentPos){
        for (File file: nodes) {
            if ((file.isFile()) || (file.listFiles().length == 0)) {
                if(!file.getName().startsWith(".")) {
                    Position<File> nodeAdded = this.tree.add(file, parentPos);
                    this.nodeList.add(nodeAdded);
                }
            }else{
                if(!file.getName().startsWith(".")) {
                    Position<File> nodeAdded = this.tree.add(file, parentPos);
                    this.nodeList.add(nodeAdded);
                    loadFileSystemAux(file.listFiles(), nodeAdded);
                }
            }
        }
    }

    private String tabs(String actualPath){
        String result = "";
        String rootPath = this.tree.root().getElement().getPath();
        String pathWithoutRoot = actualPath.replace(rootPath, "");
        for(int i = 0; i < pathWithoutRoot.length() ; i++){
                if((pathWithoutRoot.charAt(i) == '\\')||(pathWithoutRoot.charAt(i) == '/')){
                    result += "\t";
                }
        }
        return result;
    }

    public String getFileSystem() {
        String result = "";
        String tabs;
        int index;
        PreorderIterator<File> preorderIterator;
        if(this.tree.isEmpty()){
            throw new RuntimeException("Tree is empty!");
        }else{
            preorderIterator = new PreorderIterator<>(this.tree);
            while (preorderIterator.hasNext()) {
                Position<File> actualFile = preorderIterator.next();
                index = this.nodeList.lastIndexOf(actualFile);
                tabs = this.tabs(actualFile.getElement().getPath());
                result += index + " " + tabs + actualFile.getElement().getName() + "\n";
            }
        }
        return result;
    }

    public void moveFileById(int idFile, int idTargetFolder) {
        //error al hacer el getVFS pq no actualiza rutas de los nodos al  moverlos
        Position<File> pOrigin = this.nodeList.get(idFile);
        Position<File> pDest = this.nodeList.get(idTargetFolder);
        /*if(idFileArbol<idTargetFolderArbol) esta excepcion no esta bien recorrer y comparar indices
            throw new RuntimeException("A file can't be a subdirectory of itself.");
        else*/ if(!pDest.getElement().isDirectory())
            throw new RuntimeException("Target can't be a file.");
        try {
            this.tree.moveSubtree(pOrigin, pDest);
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
    }

    public void removeFileById(int idFile) {
        Position<File> pDelete = this.nodeList.get(idFile);
        try {
            this.tree.remove(pDelete);
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
    }


    public Iterable<String> findBySubstring(int idStartFile, String substring) {
        List<String> result = new ArrayList<>();
        Position<File> start = this.nodeList.get(idStartFile);
        try {
            PreorderIterator<File> preorderIterator = new PreorderIterator<>(this.tree, start);
            while (preorderIterator.hasNext()) {
                String actualfile = preorderIterator.next().getElement().getName();
                if (actualfile.contains(substring)) {
                    result.add(actualfile);
                }
            }
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
        return result;
    }

    public Iterable<String> findBySize(int idStartFile, long minSize, long maxSize) {
        throw new RuntimeException("Not yet implemented");
    }

    public String getFileVirtualPath(int idFile) {
        throw new RuntimeException("Not yet implemented");
    }

    public String getFilePath(int idFile) {
        Position<File> position = this.nodeList.get(idFile);
        try {
            //se fuerza que haga check position
            this.tree.isRoot(position);
            String path = position.getElement().getPath();
            path = path.replace('\\','/');
            return path;
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
    }

    /*private void listString(){
        int i=0;
        for(File f : this.nodeList) {
            System.out.println(i + f.getName());
            i++;
        }
    }*/

}
