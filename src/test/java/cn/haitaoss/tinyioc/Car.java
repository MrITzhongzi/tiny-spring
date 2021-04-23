package cn.haitaoss.tinyioc;

import cn.haitaoss.tinyioc.beans.annotation.Autowired;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-22 19:07
 *
 */
public class Car implements Driveable {
    private String name;
    @Autowired
    private Liveable address;
    private int price;

    public Car() {
    }

    public Car(String name, Liveable address) {
        this.name = name;
        this.address = address;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", address=" + address +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Liveable getAddress() {
        return address;
    }

    public void setAddress(Liveable address) {
        this.address = address;
    }

    @Override
    public void running() {
        System.out.println("car is running");
    }

/*   public void init(){
       System.out.println("car use init()");
   }*/
}