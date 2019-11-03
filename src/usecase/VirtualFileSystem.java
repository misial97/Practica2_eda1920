package usecase;

import material.Position;
import material.tree.iterators.PreorderIterator;
import material.tree.narytree.LinkedTree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * Realizado por Miguel Sierra
* */

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

    private String tabs(Position<File> filePosition){
        if(this.tree.isRoot(filePosition)){
            return "";
        }else{
            Position<File> parent = this.tree.parent(filePosition);
            return tabs(parent) + "\t";
        }
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
                tabs = this.tabs(actualFile);
                result += index + " " + tabs + actualFile.getElement().getName() + "\n";
            }
        }
        return result;
    }

    public void moveFileById(int idFile, int idTargetFolder) {
        //error al hacer el getVFS (solo las tabulaciones) pq no actualiza rutas de los nodos al  moverlos
        Position<File> pOrigin = this.nodeList.get(idFile);
        Position<File> pDest = this.nodeList.get(idTargetFolder);
        try{
            //forzamos checkPosition
            this.tree.isRoot(pOrigin);
            this.tree.isRoot(pDest);
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
        int idFileArbol = 0;
        int idTargetFolderArbol = 0;
        int i = 0;
            PreorderIterator<File> preorderIterator = new PreorderIterator<>(this.tree);
            while (preorderIterator.hasNext()) {
                Position<File> actualFile = preorderIterator.next();
                if(pOrigin == actualFile){
                    idFileArbol = i;
                }else if(pDest == actualFile){
                    idTargetFolderArbol = i;
                }
                i++;
            }
            if(idFileArbol<idTargetFolderArbol)
                throw new RuntimeException("A file can't be a subdirectory of itself.");
            else if(!pDest.getElement().isDirectory())
                throw new RuntimeException("Target can't be a file.");

            this.tree.moveSubtree(pOrigin, pDest);
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
                Position <File> actualFilePos = preorderIterator.next();
                String actualfileName = actualFilePos.getElement().getName();
                int index = this.nodeList.indexOf(actualFilePos);
                if (actualfileName.contains(substring)) {
                    result.add(index + "\t" + actualfileName);
                }
            }
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
        return result;
    }

    public Iterable<String> findBySize(int idStartFile, long minSize, long maxSize) {
        if(minSize > maxSize){
            throw new RuntimeException("Invalid range.");
        }
        List<String> result = new ArrayList<>();
        Position<File> start = this.nodeList.get(idStartFile);
        try {
            PreorderIterator<File> preorderIterator = new PreorderIterator<>(this.tree, start);
            while (preorderIterator.hasNext()) {
                Position <File> actualFilePos = preorderIterator.next();
                String actualfileName = actualFilePos.getElement().getName();
                int index = this.nodeList.indexOf(actualFilePos);
                long fileSize = actualFilePos.getElement().length();
                if ((actualFilePos.getElement().isFile()) && (fileSize >= minSize) && (fileSize <= maxSize)) {
                    result.add(index + "\t" + actualfileName);
                }
            }
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
        return result;
    }

    public String getFileVirtualPath(int idFile) {
        String result = "vfs:";
        Position<File> filePosition = this.nodeList.get(idFile);
        String fileName = "/" + filePosition.getElement().getName();
        try{
            if(this.tree.isRoot(filePosition)){
                return result + fileName;
            }else{
                Position parentPosition = this.tree.parent(filePosition);
                return getFileVirtualPathAux(fileName, parentPosition);
            }
        }catch (IllegalStateException e){
            throw new RuntimeException("Invalid ID.");
        }
    }

    private String getFileVirtualPathAux(String path, Position<File> pos){
        String result;
        String fileActualName = "/" + pos.getElement().getName();
        if(this.tree.isRoot(pos)){
            return "vfs:" + fileActualName + path;
        }else{
            result = fileActualName + path;
            Position<File> parentPos = this.tree.parent(pos);
            return getFileVirtualPathAux(result, parentPos);
        }
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
