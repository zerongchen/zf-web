package com.aotain.zongfen.exception;

import com.aotain.zongfen.model.dataimport.ImportResultList;

import java.util.List;

public class ImportException extends Exception{

    private static final long serialVersionUID = 1L;

    private ImportResultList importResultList;
    private List<ImportResultList> importResultLists;

    public ImportException( String msg) {
        super(msg);
    }

    /**
     * 多文件
     * @param importResultLists
     */
    public ImportException(  List<ImportResultList> importResultLists){
        this.importResultLists=importResultLists;
    }

    /**
     * 单文件
     * @param importResultList
     */
    public ImportException(  ImportResultList importResultList){
        this.importResultList=importResultList;
    }

    public ImportResultList getImportResultList() {
        return importResultList;
    }

    public List<ImportResultList> getImportResultLists() {
        return importResultLists;
    }

}
