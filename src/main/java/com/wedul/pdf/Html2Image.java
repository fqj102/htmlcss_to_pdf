package com.wedul.pdf;

import gui.ava.html.image.generator.HtmlImageGenerator;
public class Html2Image {
    public static void main(String[] args) {
        HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
        imageGenerator.loadUrl("http://localhost:6611/checkList1.html");
        imageGenerator.saveAsImage("2.png");
//        imageGenerator.saveAsImage("hello-world.jpeg");
    }
}
