package cn.haitaoss.tinyioc.beans.converter;

import cn.haitaoss.tinyioc.Point;

import java.lang.reflect.Type;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 19:44
 *
 */
public class PointConverter implements Converter<Point> {
    @Override
    public Type getType() {
        return Point.class;
    }

    @Override
    public String print(Point fieldValue) {
        return fieldValue.getX() + ";" + fieldValue.getY();
    }

    @Override
    public Point parse(String clientValue) throws Exception {
        String[] xy = clientValue.split(";");
        Point point = new Point();
        point.setX(Integer.valueOf(xy[0]));
        point.setY(Integer.valueOf(xy[1]));
        return point;
    }
}
