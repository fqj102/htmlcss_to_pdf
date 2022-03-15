package com.wedul.pdf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.PageSize;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

@Component
public class PDFView extends AbstractITextPdfView {

	private static String PDF_FILE = "d:\\work\\test1.pdf";

    @SuppressWarnings({ "static-access", "deprecation", "unchecked" })
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
		PdfWriter.getInstance(document, response.getOutputStream());
		String fileName = String.valueOf(model.get("fileName"));

		// 파일 다운로드 설정
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setContentType("application/pdf");
		response.setHeader("Content-Transper-Encoding", "binary");
		response.setHeader("Content-Disposition", "inline; filename= sss.pdf" );
		 makePdf();

		try (FileInputStream fis = new FileInputStream(PDF_FILE);
			 OutputStream out = response.getOutputStream();) {
			// saveFileName을 파라미터로 넣어 inputStream 객체를 만들고
			// response에서 파일을 내보낼 OutputStream을 가져와서
			int readCount = 0;
			byte[] buffer = new byte[1024];
			// 파일 읽을 만큼 크기의 buffer를 생성한 후
			while ((readCount = fis.read(buffer)) != -1) {
				out.write(buffer, 0, readCount);
				// outputStream에 씌워준다
			}
		} catch (Exception ex) {
			throw new RuntimeException("file Load Error");
		}

    }


	public static void makePdf() {
		// PDF를 작성하는 html
		String html = "<html>" +
				"<head></head>" +
				"<body>" +
				"<div>Hello world</div>" +
				"<div>명월입니다.</div>" +
				"</body>" +
				"</html>";
		// 파일 IO 스트림을 취득한다.
		try (FileOutputStream os = new FileOutputStream(PDF_FILE)) {
			// Pdf형식의 document를 생성한다.
			Document document = new Document(PageSize.A4, 10, 10, 10, 10);
			// PdfWriter를 취득한다.
			PdfWriter writer = PdfWriter.getInstance(document, os);
			// document Open한다.
			document.open();

			String scss1 = PDFView_bak.class.getClassLoader().getResource("css1.css").getPath();
			String scss2 = PDFView_bak.class.getClassLoader().getResource("css2.css").getPath();
			String sfont = PDFView_bak.class.getClassLoader().getResource("malgun.ttf").getPath();


			// css를 설정할 resolver 인스턴스 생성
			StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver();
			// Css 파일 설정 (css1.css 파일 설정)
			try (FileInputStream cssStream = new FileInputStream(scss1)) {
				cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
			}
			// Css 파일 설정 (css2.css 파일 설정)
			try (FileInputStream cssStream = new FileInputStream(scss2)) {
				cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
			}
			// 폰트 설정
			XMLWorkerFontProvider font = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
			// window 폰트 설정
			font.register(sfont, "MalgunGothic");
			// 폰트 인스턴스를 생성한다.
			CssAppliersImpl cssAppliers = new CssAppliersImpl(font);
			//htmlContext의 pipeline 생성. (폰트 인스턴스 생성)
			HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
			htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
			// pdf의 pipeline 생성.
			PdfWriterPipeline pdfPipeline = new PdfWriterPipeline(document, writer);
			// Html의pipeline을 생성 (html 태그, pdf의 pipeline설정)
			HtmlPipeline htmlPipeline = new HtmlPipeline(htmlContext, pdfPipeline);
			// css의pipeline을 합친다.
			CssResolverPipeline cssResolverPipeline = new CssResolverPipeline(cssResolver, htmlPipeline);
			//Work 생성 pipeline 연결
			XMLWorker worker = new XMLWorker(cssResolverPipeline, true);
			//Xml 파서 생성(Html를 pdf로 변환)
			XMLParser xmlParser = new XMLParser(true, worker, Charset.forName("UTF-8"));
			// 출력한다.
			try (StringReader strReader = new StringReader(html)) {
				xmlParser.parse(strReader);
			}
			// document의 리소스 반환
			document.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}