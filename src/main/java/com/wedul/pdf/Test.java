package com.wedul.pdf;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

public abstract class Test {
 @SuppressWarnings("serial")
 static class Kit extends HTMLEditorKit {
  public Document createDefaultDocument() {
   HTMLDocument doc = (HTMLDocument) super.createDefaultDocument();
   doc.setTokenThreshold(Integer.MAX_VALUE);
   doc.setAsynchronousLoadPriority(-1);
   return doc;
  }
 }

 public static BufferedImage create(String src, int width, int height) {
  BufferedImage image = null;
  JEditorPane pane = new JEditorPane();
  Kit kit = new Kit();
  pane.setEditorKit(kit);
  pane.setEditable(false);
  pane.setMargin(new Insets(0,20,0,20));
  pane.setContentType("text/html; charset=UTF-8");
  //pane.setFont(new Font("serif",Font.BOLD,20));

  try {
   pane.setPage(new URL(src));
   // HTML 내용을 콘솔창 출력
   System.out.println(pane.getText());
   image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
   Graphics g = image.createGraphics();
   g.setColor(Color.WHITE);
   Container c = new Container();
   SwingUtilities.paintComponent(g, pane, c, 0, 0, width, height);
   g.dispose();
  } catch (Exception e) {
   System.out.println(e);
  }
  return image;
 }

 public static  void main ( String [] args ) {
  BufferedImage ire;
  // 서버의 웹서버로 접근
  String url="http://localhost:6611/checkList1.html";
  //String url="file:///D:\\print.html"; 서버의 파일을 부를 때
  // 저장될 이미지 위치와 이름이 명
  String path="/Users/fqj/_2022_project/tmp1.jpg";
  // 이미지 크기
  ire = Test.create(url, 800, 800);
  try{
   ImageIO.write(ire, "PNG", new File(path));
  }catch(IOException e){
   e.printStackTrace();
  }catch(NullPointerException e){
   e.printStackTrace();
  }catch(IllegalArgumentException e){
   e.printStackTrace();
  }
 }
}


