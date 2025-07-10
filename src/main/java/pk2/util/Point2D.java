package pk2.util;

public class Point2D {
    public double x;
    public double y;

    public Point2D(double x,double y){
        this.x = x;
        this.y = y;
    }


    public Point2D(){
        this.x = 0;
        this.y = 0;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public boolean equals(Point2D second){
        return second!=null && second.x == this.x && second.y == this.y;
    }
}
