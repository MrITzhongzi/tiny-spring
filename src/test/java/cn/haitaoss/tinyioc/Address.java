package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-22 19:06
 *
 */
public class Address implements Liveable {
    private String local;
    private Driveable car;

    public Driveable getCar() {
        return car;
    }

    public void setCar(Driveable car) {
        this.car = car;
    }

    public Address() {
    }

    @Override
    public String toString() {
        return "Address{" +
                "local='" + local + '\'' +
                ", car=" + car +
                '}';
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    @Override
    public void living() {
        System.out.println("address is living");
    }
}
