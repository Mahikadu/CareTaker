package com.hdfc.app42service;

import android.content.Context;

import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;

import org.json.JSONObject;

public class StorageService {

    private AsyncApp42ServiceApi asyncService;

    public StorageService(Context context) {
        try {
            asyncService = AsyncApp42ServiceApi.instance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public void setOtherMetaHeaders(HashMap<String, String> otherMetaHeaders) {
        asyncService.setOtherMetaHeaders(otherMetaHeaders);
    }*/

    public void insertDocs(JSONObject jsonToSave, AsyncApp42ServiceApi.App42StorageServiceListener
            app42CallBack, String collectionCustomer) {
        try {
            asyncService.insertJSONDoc(Config.dbName, collectionCustomer, jsonToSave, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllDocs(String strCollectionName,
                              App42CallBack app42CallBack) {
        try {
            asyncService.deleteAllDocs(Config.dbName, strCollectionName, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findDocsById(String strDocId, String strCollectionName, AsyncApp42ServiceApi.
            App42StorageServiceListener app42CallBack) {
        try {
            asyncService.findDocByDocId(Config.dbName, strCollectionName, strDocId, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findDocsByIdApp42CallBack(String strDocId, String strCollectionName, App42CallBack
            app42CallBack) {
        try {
            asyncService.findDocByDocIdApp42CallBack(Config.dbName, strCollectionName, strDocId,
                    app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findDocsByKeyValue(String strCollectionName, String strKey, String strValue,
                                   AsyncApp42ServiceApi.App42StorageServiceListener app42CallBack) {
        try {
            asyncService.findDocumentByKeyValue(Config.dbName, strCollectionName, strKey, strValue,
                    app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findDocsByQuery(String strCollectionName, Query query,
                                App42CallBack app42CallBack) {
        try {
            asyncService.findDocumentByQuery(Config.dbName, strCollectionName, query, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findDocsByQueryOrderBy(String strCollectionName, Query query, int max, int offset,
                                       String strKey, int iOrderFlag, App42CallBack app42CallBack) {
        try {
            asyncService.findDocumentByQueryPagingOrderBy(Config.dbName, strCollectionName, query,
                    max, offset, strKey, iOrderFlag, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findAllDocs(String strCollectionName, App42CallBack app42CallBack) {
        try {
            asyncService.findAllDocuments(Config.dbName, strCollectionName, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void updateDocs(JSONObject jsonToUpdate, String fieldName, String checkValue) {
        asyncService.updateDocByKeyValue(Config.dbName, Config.collectionCustomer, fieldName, checkValue, jsonToUpdate, this);
    }*/

    public void updateDocs(JSONObject jsonToUpdate, String strDocId, String strCollectionName,
                           App42CallBack app42CallBack) {
        try {
            asyncService.updateDocPartByKeyValue(Config.dbName, strCollectionName, strDocId,
                    jsonToUpdate, app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDocById(String strCollectionName, String strDocId,
                              App42CallBack app42CallBack) {
        try {
            asyncService.deleteDocById(Config.dbName, strCollectionName, strDocId,
                    app42CallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
