package bclb.upload.album.albumlcc.bean;

/**
 * Created by cuiliubi on 2017/9/27.
 * 显示的图片和视频用到的类
 */

public class DisplayInfoBean {
    private String path;
    private String addTime;

    public DisplayInfoBean(String path, String addTime) {
        this.path = path;
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "DisplayInfoBean{" +
                "path='" + path + '\'' +
                ", addTime='" + addTime + '\'' +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

}
