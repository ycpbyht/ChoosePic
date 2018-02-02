package bclb.upload.album.model;

import java.io.Serializable;

/**
 * Created by Chong on 2016/7/26.
 * 图片变换地址时候，对应值，
 */
public class PicCompress implements Serializable {
    private String picadress; //选择的图片本地地址(视频的地址)
    private String pic2com;//图片压缩后地址(而后存入传回的视频的地址)
    boolean isCom; //是否进行了压缩
    private String name4suc;//上传后服务器返回的图片名称（视频的名字）
    private String foodLivePic; //存放视频的图片地址（存放图片的地址）
    private boolean hasup;//已经上传

    //应发动态需要，加两个字段
    private String foodlivepicname;
    private int foodliveLength;


    public PicCompress() {
    }

    /**
     * @param picadress 图片源地址
     * @param pic2com   图片压缩后地址
     * @param isCom     是否进行了压缩
     * @param name4suc  上传后服务器返回的图片名称
     * @param hasup     已经上传
     */

    public PicCompress(String picadress, String pic2com, boolean isCom, String name4suc, boolean hasup) {
        this.picadress = picadress;
        this.pic2com = pic2com;
        this.isCom = isCom;
        this.name4suc = name4suc;
        this.hasup = hasup;
    }

    public PicCompress(String picadress, String pic2com, boolean isCom) {
        this.picadress = picadress;
        this.pic2com = pic2com;
        this.isCom = isCom;
        this.hasup=false;
        this.name4suc="";
    }
    public PicCompress(String picadress, boolean hasup, String pic2com, String name4suc) {
        this.picadress = picadress;
        this.pic2com = pic2com;
        this.hasup = hasup;
        this.isCom=false;
        this.name4suc=name4suc;

    }

    public PicCompress(String picadress, String pic2com, String name4suc, String foodLivePic, boolean hasup) {
        this.picadress = picadress;
        this.pic2com = pic2com;
        this.name4suc = name4suc;
        this.foodLivePic = foodLivePic;
        this.hasup = hasup;
    }

    public PicCompress(String picadress, String pic2com) {

        this.picadress = picadress;
        this.pic2com = pic2com;
        this.isCom = false;
        this.name4suc = "";
        this.hasup = false;
    }

    public String getFoodLivePic() {
        return foodLivePic;
    }

    public void setFoodLivePic(String foodLivePic) {
        this.foodLivePic = foodLivePic;
    }

    public boolean isHasup() {
        return hasup;
    }

    public void setHasup(boolean hasup) {
        this.hasup = hasup;
    }

    public String getPicadress() {
        return picadress;
    }

    public void setPicadress(String picadress) {
        this.picadress = picadress;
    }

    public String getPic2com() {
        return pic2com;
    }

    public void setPic2com(String pic2com) {
        this.pic2com = pic2com;
    }

    public boolean isCom() {
        return isCom;
    }

    public void setCom(boolean com) {
        isCom = com;
    }

    public String getName4suc() {
        return name4suc;
    }

    public void setName4suc(String name4suc) {
        this.name4suc = name4suc;
    }

    public String getFoodlivepicname() {
        return foodlivepicname;
    }

    public void setFoodlivepicname(String foodlivepicname) {
        this.foodlivepicname = foodlivepicname;
    }

    public int getFoodliveLength() {
        return foodliveLength;
    }

    public void setFoodliveLength(int foodliveLength) {
        this.foodliveLength = foodliveLength;
    }

    @Override
    public String toString() {
        return "PicCompress{" +
                "picadress='" + picadress + '\'' +
                ", pic2com='" + pic2com + '\'' +
                ", isCom=" + isCom +
                ", name4suc='" + name4suc + '\'' +
                ", hasup=" + hasup +
                '}';
    }
}
