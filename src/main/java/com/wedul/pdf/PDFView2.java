package com.wedul.pdf;

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
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;

@Component
public class PDFView2 extends AbstractITextPdfView {

    @SuppressWarnings({ "static-access", "deprecation", "unchecked" })
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
		PdfWriter.getInstance(document, response.getOutputStream());
		String fileName = String.valueOf(model.get("fileName"));

		// 파일 다운로드 설정
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setContentType("application/pdf");

		// Document 오픈
		document.open();

		try {
			// Document 생성

			// PdfWriter 생성
			// PdfWriter writer = PdfWriter.getInstance(document, new
			// FileOutputStream("d:/test.pdf")); // 바로 다운로드.
//			writer.setInitialLeading(12.5f);
			PdfWriter.getInstance(document, response.getOutputStream());

			// 파일 다운로드 설정
			response.setContentType("application/pdf");

			response.setHeader("Content-Transper-Encoding", "binary");
			response.setHeader("Content-Disposition", "inline; filename= sss.pdf" );

			String html = "<html>" +
					"<head></head>" +
					"<body>" +
					"<div>Hello world</div>" +
					"<div>명월입니다.</div>" +
					"</body>" +
					"</html>";

			// Pdf형식의 document를 생성한다.
//			Document document = new Document(PageSize.A4, 10, 10, 10, 10); // PdfWriter를 취득한다.
//			PdfWriter writer = PdfWriter.getInstance(document, os);
			// document Open한다.
			document.open();
			// css를 설정할 resolver 인스턴스 생성
			StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver();
			// Css 파일 설정 (css1.css 파일 설정)
			try (FileInputStream cssStream = new FileInputStream("d:\\work\\css1.css")) {
				cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
			}
			// Css 파일 설정 (css2.css 파일 설정)
			try (FileInputStream cssStream = new FileInputStream("d:\\work\\css2.css")) {
				cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
			}
			// 폰트 설정
			XMLWorkerFontProvider font = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
			// window 폰트 설정
			font.register("d:\\malgun.ttf", "MalgunGothic");
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
			document.close();
			writer.close();
		} catch (Exception e) {
			throw e;
		}

    }
}