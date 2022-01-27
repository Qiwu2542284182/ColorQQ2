package me.qiwu.colorqq.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.adapter.FileSelectAdapter;
import me.qiwu.colorqq.drawable.PicassoTarget;
import me.qiwu.colorqq.library.Layer.Alignment;
import me.qiwu.colorqq.library.Layer.AnimHelper;
import me.qiwu.colorqq.library.Layer.AnyLayer;
import me.qiwu.colorqq.library.Layer.LayerManager;
import me.qiwu.colorqq.util.PicassoUtil;
import me.qiwu.colorqq.util.StatusBarUtil;
import me.qiwu.colorqq.widget.TitleBar;

public class PhotoPickActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    private static final String[] IMAGE_PROJECTION = {"_id", "_data", "bucket_id", "bucket_display_name", "date_modified"};
    public static final int CODE = 9998;
    public static final String TAG_PHOTO_PATH = "photo_paths";
    private static final int DIALOG_BG_COLOR = 0x66000000;
    private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static int sFinalPhotoDicCode = -1;
    private ViewGroup mPhotoDirectoryNameLayout;
    private TextView mPhotoDirectoryNameTextView;
    private TextView mCurrentNumTextView;
    private TextView mMaxNumTextView;
    private List<PhotoDirectory> mPhotoDirectory;
    private PhotoDirectory mCurrentDirectory;
    private RecyclerView mRecyclerView;
    private View mDivide;
    private PicAdapter mAdapter;
    private TitleBar mTitleBar;
    private boolean mIsShowDialog;
    private AnyLayer mDialog;
    private int mMaxNum;
    private List<String> mSelectedItem = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaxNum = getIntent().getIntExtra("num",1);
        setContentView(R.layout.activity_photo_pick);
        mDivide = findViewById(R.id.photo_pick_divide);
        mPhotoDirectoryNameLayout = findViewById(R.id.photo_pick_dicName_layout);
        mPhotoDirectoryNameTextView = findViewById(R.id.photo_pick_dicName);
        mMaxNumTextView = findViewById(R.id.photo_pick_maxNum);
        mCurrentNumTextView = findViewById(R.id.photo_pick_currentNum);
        mCurrentNumTextView.setText(String.valueOf(0));
        mMaxNumTextView.setText(String.valueOf(mMaxNum));
        mTitleBar = findViewById(R.id.photo_pick_titleBar);
        mTitleBar.setRightText("确定", v -> {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(TAG_PHOTO_PATH,new ArrayList<>(mSelectedItem));
            setResult(Activity.RESULT_OK,intent);
            finish();
        });
        mTitleBar.setRightTextEnable(false);
        mRecyclerView = findViewById(R.id.photo_pick_recyclerView);
        mPhotoDirectory = getPhotos(getContext());
        if (sFinalPhotoDicCode == -1){
            mCurrentDirectory = mPhotoDirectory.get(0);
        } else {
            for (int i =0;i<mPhotoDirectory.size();i++){
                PhotoDirectory photoDirectory = mPhotoDirectory.get(i);
                if (photoDirectory.hashCode() == sFinalPhotoDicCode){
                    mCurrentDirectory = photoDirectory;
                    break;
                }
            }
        }
        if (mCurrentDirectory == null){
            mCurrentDirectory = mPhotoDirectory.get(0);
        }
        mPhotoDirectoryNameTextView.setText(mCurrentDirectory.name);
        mAdapter = new PicAdapter(new ArrayList<>(mCurrentDirectory.getPhotoPaths()),mSelectedItem);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        mRecyclerView.setAdapter(mAdapter);
        mPhotoDirectoryNameLayout.setOnClickListener(v -> {
            if (mIsShowDialog){
                if (mDialog != null){
                    mDialog.dismiss();
                    mDialog = null;
                }
                return;
            }
            mIsShowDialog = true;
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setBackgroundColor(Color.WHITE);

            ListView listView = new ListView(getContext());
            listView.setScrollBarSize(0);
            listView.setDividerHeight(0);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                mCurrentDirectory = mPhotoDirectory.get(position);
                mPhotoDirectoryNameTextView.setText(mCurrentDirectory.name);
                mAdapter.replaceData(new ArrayList<>(mCurrentDirectory.getPhotoPaths()));
                if (mDialog != null){
                    mDialog.dismiss();
                    mDialog = null;
                    mIsShowDialog = false;
                }
            });
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            listView.setAdapter(new DicListAdapter(mPhotoDirectory,mCurrentDirectory,getContext()));
            listView.setSelection(mPhotoDirectory.indexOf(mCurrentDirectory));
            WindowManager windowManager = getWindowManager();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int heightPixels = displayMetrics.heightPixels / 2;
            linearLayout.addView(listView,-1,heightPixels);
            mDialog = AnyLayer.target(mDivide)
                    .alignment(Alignment.Direction.VERTICAL, Alignment.Horizontal.ALIGN_LEFT, Alignment.Vertical.ABOVE,true)
                    .backgroundColorInt(DIALOG_BG_COLOR)
                    .contentView(linearLayout)
                    .contentAnim(new LayerManager.IAnim() {
                        @Override
                        public Animator inAnim(View target) {
                            return AnimHelper.createBottomInAnim(target);
                        }

                        @Override
                        public Animator outAnim(View target) {
                            return AnimHelper.createBottomOutAnim(target);
                        }
                    })
                    .onLayerDismissListener(new LayerManager.OnLayerDismissListener() {
                        @Override
                        public void onDismissing(AnyLayer anyLayer) {

                        }

                        @Override
                        public void onDismissed(AnyLayer anyLayer) {
                            mIsShowDialog = false;
                            mDialog = null;
                        }
                    })
                    .gravity(Gravity.BOTTOM);
            mDialog.show();
        });
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        sFinalPhotoDicCode = mCurrentDirectory.hashCode();
        super.onDestroy();
    }

    public static List<PhotoDirectory> getPhotos(Context context) {
        Cursor createCursor = createCursor(context);
        if (createCursor == null) {
            ArrayList<PhotoDirectory> photoList = new ArrayList<>();
            PhotoDirectory defDirectory = new PhotoDirectory();
            defDirectory.setName("全部图片");
            defDirectory.setId("ALL");
            defDirectory.photos = new ArrayList<>();
            return photoList;
        }
        List<PhotoDirectory> dataFromCursor = getDataFromCursor(context, createCursor);
        createCursor.close();
        return dataFromCursor;
    }

    private static Cursor createCursor(Context context) {
        return context.getContentResolver().query(IMAGE_URI, IMAGE_PROJECTION, "(_size > 0 or _size is null)", (String[]) null, "date_modified DESC");
    }

    private static List<PhotoDirectory> getDataFromCursor(Context context, Cursor cursor) {
        ArrayList<PhotoDirectory> photoList = new ArrayList<>();
        PhotoDirectory defDirectory = new PhotoDirectory();
        defDirectory.setName("全部图片");
        defDirectory.setId("ALL");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String bucketId = cursor.getString(cursor.getColumnIndexOrThrow("bucket_id"));
            String bucketDisplayName = cursor.getString(cursor.getColumnIndexOrThrow("bucket_display_name"));
            String data = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            long dateModified = cursor.getLong(cursor.getColumnIndexOrThrow("date_modified"));
            PhotoDirectory photoDirectory = new PhotoDirectory();
            photoDirectory.setId(emptyIfNull(bucketId));
            photoDirectory.setName(emptyIfNull(bucketDisplayName));
            if (!photoList.contains(photoDirectory)) {
                photoDirectory.setCoverPath(data);
                photoDirectory.addPhoto(id, data, dateModified);
                photoDirectory.setDateAdded(dateModified);
                photoList.add(photoDirectory);
            } else {
                (photoList.get(photoList.indexOf(photoDirectory))).addPhoto(id, data, dateModified);
            }
            defDirectory.addPhoto(id, data, dateModified);
        }
        if (defDirectory.getPhotoPaths().size() > 0) {
            defDirectory.setCoverPath((String) defDirectory.getPhotoPaths().get(0));
        }
        photoList.add(0, defDirectory);
        return photoList;
    }

    public static void startActivity(Activity activity,int max){
        Intent intent = new Intent(activity,PhotoPickActivity.class);
        intent.putExtra("num",max);
        activity.startActivityForResult(intent,CODE);
    }

    public static void startActivity(Fragment fragment, int max){
        Intent intent = new Intent(fragment.getContext(),PhotoPickActivity.class);
        intent.putExtra("num",max);
        fragment.startActivityForResult(intent,CODE);
    }

    public static List<String> obtain(Intent intent){
        List<String> paths = intent.getStringArrayListExtra(TAG_PHOTO_PATH);
        if (paths == null){
            return new ArrayList<>();
        }
        return paths;
    }

    private static String emptyIfNull(String s){
        return s == null ? "" : s;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        String path = mCurrentDirectory.photos.get(position).path;
        if (mSelectedItem.contains(path)){
            mSelectedItem.remove(path);
        } else {
            if (mSelectedItem.size() < mMaxNum){
                mSelectedItem.add(path);
            } else {
                Toast.makeText(getContext(),"最多只能选择" + mMaxNum + "张图片",Toast.LENGTH_SHORT).show();
            }
        }
        mTitleBar.setRightTextEnable(!mSelectedItem.isEmpty());
        mCurrentNumTextView.setText(String.valueOf(mSelectedItem.size()));
        mAdapter.notifyDataSetChanged();
    }

    static class PicAdapter extends BaseItemDraggableAdapter<String,BaseViewHolder> {
        private List<String> mSelectItem;
        public PicAdapter(List<String> data,List<String> selectItem) {
            super(R.layout.item_photo_pick,data);
            this.mSelectItem = selectItem;
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            ImageView imageView = helper.getView(R.id.photo_pick_pre);
            PicassoUtil.loadFile(imageView,item);
            helper.setVisible(R.id.item_photo_gif,item.endsWith(".gif"));
            helper.setBackgroundRes(R.id.item_photo_check,mSelectItem.contains(item) ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        }

    }

    static class PhotoDirectory {
        private String coverPath;
        private long dateAdded;
        private String id;
        private String name;
        private List<Photo> photos = new ArrayList<>();

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PhotoDirectory)) {
                return false;
            }
            PhotoDirectory photoDirectory = (PhotoDirectory) obj;
            if (!id.equals(photoDirectory.id)) {
                return false;
            }
            return name.equals(photoDirectory.name);
        }

        public int hashCode() {
            return (id.hashCode() * 31) + name.hashCode();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCoverPath() {
            return coverPath;
        }

        public void setCoverPath(String coverPath) {
            this.coverPath = coverPath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getDateAdded() {
            return dateAdded;
        }

        public void setDateAdded(long dateAdded) {
            this.dateAdded = dateAdded;
        }

        public List<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(List<Photo> photos) {
            this.photos = photos;
        }

        public List<String> getPhotoPaths() {
            ArrayList<String> arrayList = new ArrayList<>(photos.size());
            for (Photo path : photos) {
                arrayList.add(path.getPath());
            }
            return arrayList;
        }

        public void addPhoto(int id, String path, long dateAdded) {
            this.photos.add(new Photo(id, path, dateAdded));
        }
    }


     static class Photo {
        private long dateAdded;
        private int id;
        private String path;

        public Photo(int id, String path, long dateAdded) {
            this.id = id;
            this.path = path;
            this.dateAdded = dateAdded;
        }

        public String getPath() {
            return path;
        }

        public int getId() {
            return id;
        }

        public long getDateAdded() {
            return dateAdded;
        }
    }

    static class DicListAdapter extends BaseAdapter{
        private List<PhotoDirectory> mData;
        private PhotoDirectory mCurrentDic;
        private Context mContext;
        public DicListAdapter(List<PhotoDirectory> data,PhotoDirectory photoDirectory,Context context){
            mData = data;
            mCurrentDic = photoDirectory;
            mContext = context;
        }
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_pick_dic_list,null);
                viewHolder = new ViewHolder();
                viewHolder.prePic = view.findViewById(R.id.photo_pick_dic_list_pre);
                viewHolder.name =  view.findViewById(R.id.photo_pick_dic_list_name);
                viewHolder.num =  view.findViewById(R.id.photo_pick_dic_list_num);
                viewHolder.check = view.findViewById(R.id.photo_pick_dic_list_check);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }
            PhotoDirectory photoDirectory = mData.get(position);
            Picasso.get().load(new File(photoDirectory.coverPath)).fit().centerCrop().into(viewHolder.prePic);
            viewHolder.name.setText(photoDirectory.name);
            viewHolder.num.setText(String.valueOf(photoDirectory.photos.size()));
            viewHolder.check.setVisibility(mCurrentDic.equals(photoDirectory) ? View.VISIBLE : View.INVISIBLE);
            return view;
        }


        class ViewHolder{
            ImageView prePic;
            ImageView check;
            TextView name;
            TextView num;
        }
    }

}
