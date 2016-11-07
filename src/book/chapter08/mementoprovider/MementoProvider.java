package book.chapter08.mementoprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MementoProvider extends ContentProvider {
	
    private static UriMatcher matcher=new  UriMatcher( UriMatcher.NO_MATCH);
    //定义两个常量，用于匹配URI的返回值
    private static final int MEMENTOS=1;
    private static final int MEMENTO=2;
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    static{
    	matcher.addURI(Mementos.AUTHORITY, "mementos", MEMENTOS);//添加URI匹配规则，用于判断URI的类型
    	matcher.addURI(Mementos.AUTHORITY, "memento/#", MEMENTO);
    }
    
    @Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
    	dbHelper=new MyDatabaseHelper(getContext(),"memento.db",null,1);
    	db=dbHelper.getReadableDatabase();//创建数据库工具类，并获取数据库实例
		return true;
	}
    
    //添加记录
    @Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
    	long rowID=db.insert("memento_tb", Mementos.Memento._ID, values);
    	//如果添加成功，则通知数据库记录发生更新
    	if(rowID>0){
    		Uri mementoUri=ContentUris.withAppendedId(uri, rowID);
    		getContext().getContentResolver().notifyChange(mementoUri, null);
    		return mementoUri;
    	}
		return null;
	}
    
    //更新记录
    @Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
    	int num=0;
    	switch (matcher.match(uri)){
    	case MEMENTOS:
    		num=db.update("memento_tb", values, selection, selectionArgs);
    		break;
    	case MEMENTO:
    		long id=ContentUris.parseId(uri);
    		String where=Mementos.Memento._ID+"="+id;
    		if(selection !=null&&!"".equals(selection)){
    			where =where +"and"+selection;
    		}
    		num=db.update("memento_tb", values, where, selectionArgs);
    		break;
    	default:
    		throw new IllegalArgumentException("未知Uri:"+uri);
    	}
    	getContext().getContentResolver().notifyChange(uri, null);
    	return num;
	}
    
    @Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) {
		// TODO Auto-generated method stub
    	switch(matcher.match(uri)){
    	case MEMENTOS:
    		return db.query("memento_tb", projection, selection, selectionArgs, null, null, sortOrder);
    	case MEMENTO:
    		long id=ContentUris.parseId(uri);
    		String where=Mementos.Memento._ID+"="+id;
    		if(selection !=null&&!"".equals(selection)){
    			where =where +"and"+selection;
    		}
    		return db.query("memento_tb",projection, where, selectionArgs, null, null, sortOrder);
    	default:
    		throw new IllegalArgumentException("未知Uri:"+uri);	
    	}
	}
    
    @Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
    	switch(matcher.match(uri)){
    	case MEMENTOS:
    		return "vnd.android.cursor.dir/mementos";
    	case MEMENTO:
    		return "vnd.android.cursor.item/memento";
    	default:
    		throw new IllegalArgumentException("未知Uri:"+uri);	
    	}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
