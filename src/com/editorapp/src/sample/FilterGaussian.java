package com.editorapp.src.sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FilterGaussian {

    private static int blurRadius;
    private static Image image;
    public static double[][] weightArr;

    public FilterGaussian(int blurRadius, javafx.scene.image.Image image){
        this.blurRadius = blurRadius;
        this.image = image;
        weightArr = new double[blurRadius*2+1][blurRadius*2+1];
        calculateWeightMatrix();
        getFinalWeightMatrix();
    }

    public static int getBlurRadius() {
        return blurRadius;
    }

    public static void setImage(Image image) {
        FilterGaussian.image = image;
    }

    private static double getR(int x, int y){
        return image.getPixelReader().getColor(x,y).getRed();
    }
    private static double getG(int x, int y){
        return image.getPixelReader().getColor(x,y).getGreen();
    }
    private static double getB(int x, int y){
        return image.getPixelReader().getColor(x,y).getBlue();
    }

    private static double[][] getColorMatrix(int x, int y, int whichColor){

        int startX = x-blurRadius;
        int startY = y-blurRadius;
        int counter = 0;

        int length = blurRadius*2+1;
        double[][] arr = new double[length][length];

        for (int i=startX ; i<startX+length ;i++){
            for (int j = startY; j < startY+length; j++){
                if (whichColor == 1){
                    arr[counter%length][counter/length] = getR(i,j);
                }else if (whichColor == 2){
                    arr[counter%length][counter/length] = getG(i,j);
                }else if (whichColor == 3){
                    arr[counter%length][counter/length] = getB(i,j);
                }
                counter++;
            }
        }

        return arr;
    }

    private void calculateWeightMatrix(){

        for (int i=0;i<blurRadius*2+1;i++){
            for (int j=0;j<blurRadius*2+1;j++){
                weightArr[i][j] = getWeight(j-blurRadius,blurRadius-i);
//                System.out.print(weightArr[i][j]+"\t");
            }
//            System.out.println();
        }

    }

    private static double getWeight(int x, int y){

        double sigma = (blurRadius*2+1)/2;
        double weight = (1/(2*Math.PI*sigma*sigma))*Math.pow(Math.E,((-(x*x+y*y))/((2*sigma)*(2*sigma))));

        return weight;
    }

    private void getFinalWeightMatrix(){

        int length = blurRadius*2+1;
        double weightSum = 0;

        for (int i = 0;i<length;i++){
            for (int j=0; j<length; j++ ){
                weightSum+=weightArr[i][j];
            }
        }
        for (int i = 0;i<length;i++){
            for (int j=0; j<length; j++ ){
                weightArr[i][j] = weightArr[i][j]/weightSum;
//                System.out.print(weightArr[i][j]+"\t");
            }
            //System.out.print("\n");
        }

    }

    private static double getBlurColor(int x, int y, int whichColor){

        double blurGray = 0;
        double[][] colorMat = getColorMatrix(x,y,whichColor);

        int length = blurRadius*2+1;
        for (int i = 0;i<length;i++){
            for (int j=0; j<length; j++ ){
                blurGray += weightArr[i][j]*colorMat[i][j];
            }
        }

        return blurGray;
    }

    public static WritableImage getBluredImg(Image image){
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        WritableImage bi = new WritableImage(w,h);
        for (int x = 0; x < bi.getWidth()-2*blurRadius; x++) {
            for (int y = 0; y < bi.getHeight()-2*blurRadius; y++) {
                double r = getBlurColor(blurRadius+x,blurRadius+y,1);
                double g = getBlurColor(blurRadius+x,blurRadius+y,2);
                double b = getBlurColor(blurRadius+x,blurRadius+y,3);
                Color color = new Color(r,g,b,1.0F);
                bi.getPixelWriter().setColor(x,y,color);
            }
        }
        return bi;
    }

//    public static void main(String[] args){
//        new FilterGaussian(7,null).getFinalWeightMatrix();
//    }

}
