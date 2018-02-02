package bclb.upload.album.albumlcc.bean;

/**
 * Created by cuiliubi on 18/1/12 星期四.
 */

public class Photobean {
    int select;//0 未被选中，>0 被选中，位置是selet
    String path;

    public Photobean(int select, String path) {
        this.select = select;
        this.path = path;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
