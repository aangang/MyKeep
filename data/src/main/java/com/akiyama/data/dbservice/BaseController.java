package com.akiyama.data.dbservice;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.List;

import com.akiyama.base.AppContext;
import com.akiyama.base.utils.LogUtil;
import com.akiyama.data.db.DataProviderHelper;
import com.akiyama.data.db.SQLiteHelper;
import com.akiyama.data.db.model.BaseModel;
import com.akiyama.data.db.model.IModel;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 提供基本的插入、删除、查询操作
 * @author zhiwu_yan
 * @version 1.0
 * @since 2015-07-08  13:54
 */
public abstract class BaseController implements IBaseController {

    private static final String TAG="BaseController";
    protected BriteContentResolver mBriteContentResolver;
    private Func1<Cursor, BaseModel> mBaseModeFunction;
    private final BriteDatabase mDatabaseHelper;
    private BaseModel mModel;
    public BaseController(){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(AppContext.getInstance());
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(sqLiteHelper, Schedulers.io());
        mBriteContentResolver = sqlBrite.wrapContentProvider(AppContext.getInstance().getContentResolver(), Schedulers.io());
        mBaseModeFunction = new Func1<Cursor,BaseModel>() {
            @Override
            public BaseModel call(Cursor cursor) {
                return mModel.getModel(cursor);
            }
        };
    }
    @Override
    public int insert(Context context, List<? extends BaseModel> models) {
        if(models==null && models.size()==0){
            return -1;
        }
        Uri uri=models.get(0).getContentUri();//获取Content_uri
        return context.getContentResolver().bulkInsert(uri, DataProviderHelper.getContentValuesByModels(models));
    }

    @Override
    public Uri insert(Context context, IModel model) {
        if(model==null){
            return null;
        }
        Uri uri=model.getContentUri();//获取Content_uri
        LogUtil.d(TAG,"insert"+uri);
        try{
            return context.getContentResolver().insert(uri,model.values());
        }catch (Exception e){
            LogUtil.e(TAG,"cause:"+e.getCause()+"\n"+e.getMessage());
        }
        return null;
    }


    @Override
    public Observable<BaseModel> query(Context context, String id, Class<BaseModel> tClass) {
        try {
            mModel= tClass.newInstance();
            if(mModel instanceof BaseModel){
                Uri uri= DataProviderHelper.withAppendedId((mModel.getContentUri()),id);
                return mBriteContentResolver
                        .createQuery(uri,null,null,null,null,false)
                        .mapToOne(mBaseModeFunction);
            }
        } catch (InstantiationException e) {
            LogUtil.e(TAG,"cause:"+e.getCause());
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "cause:" + e.getCause());
        }

        return null;
    }

    @Override
    public boolean deleteById(Context context, String id,Class<? extends BaseModel> tClass) {
        try {
            Uri uri=DataProviderHelper.withAppendedId(tClass.newInstance().getContentUri(),id);
            int row=context.getContentResolver().delete(uri,null,null);
            if(row>0){
              return true;
            }
        } catch (InstantiationException e) {
            LogUtil.e(TAG,"cause:"+e.getCause());
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "cause:" + e.getCause());
        }
        return false;
    }

    @Override
    public boolean deleteByIds(Context context, String[] ids,Class<? extends BaseModel> tClass) {
        SQLiteDatabase db=new SQLiteHelper(context).getReadableDatabase();
        try {
            db.beginTransaction();//开启事务
            for(int i=0;i<ids.length;i++){
                Uri uri=DataProviderHelper.withAppendedId(tClass.newInstance().getContentUri(),ids[i]);
                context.getContentResolver().delete(uri, null, null);
            }
            db.setTransactionSuccessful();//事务处理成功
            return true;
        } catch (InstantiationException e) {
            LogUtil.e(TAG,"cause:"+e.getCause());
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "cause:" + e.getCause());
        }finally {
            db.endTransaction();//事务结束
        }
        return false;
    }

    @Override
    public boolean updateById(Context context,BaseModel model) {
        Uri uri= DataProviderHelper.withAppendedId(model.getContentUri(),model.getId());
        int row=context.getContentResolver().update(uri,model.values(),null,null);
        if(row!=0){
            return true;
        }
        return false;
    }

    /**
     * 通过对应的用户ID获取对应的数据
     * @return
     */
    public abstract Observable<List<BaseModel>> getDbByUserId(Context context, String userId);
}