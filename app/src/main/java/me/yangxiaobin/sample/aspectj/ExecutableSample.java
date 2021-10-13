package me.yangxiaobin.sample.aspectj;

public class ExecutableSample {

    public void execute() {
        System.out.println("----> ExecutableSample executed");
    }


    public static void main(String[] args) {
        ExecutableSample demo = new ExecutableSample();
        demo.execute();
    }
}
