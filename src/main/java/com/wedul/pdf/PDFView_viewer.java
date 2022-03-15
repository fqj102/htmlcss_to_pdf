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
public class PDFView_viewer extends AbstractITextPdfView {

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

			// Document 오픈
			document.open();
			XMLWorkerHelper helper = XMLWorkerHelper.getInstance();

			//css
			String scss1 = PDFView_bak.class.getClassLoader().getResource("css1.css").getPath();
			String scss2 = PDFView_bak.class.getClassLoader().getResource("css2.css").getPath();
			//font
			String sfont = PDFView_bak.class.getClassLoader().getResource("malgun.ttf").getPath();

			// CSS
			StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver();
			// Css 파일 설정 (css1.css 파일 설정)
			try (FileInputStream cssStream = new FileInputStream("d:\\work\\css1.css")) {
				cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
			}
			// Css 파일 설정 (css2.css 파일 설정)
			try (FileInputStream cssStream = new FileInputStream("d:\\work\\css2.css")) {
				cssResolver.addCss(XMLWorkerHelper.getCSS(cssStream));
			}

			// HTML, 폰트 설정
			XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
			fontProvider.register(sfont, "MalgunGothic"); // MalgunGothic은

			CssAppliersImpl cssAppliers = new CssAppliersImpl(fontProvider);

			HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
			htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

			// Pipelines
			PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
			HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
			CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

			XMLWorker worker = new XMLWorker(css, true);
			XMLParser xmlParser = new XMLParser(true,worker, Charset.forName("UTF-8"));

			// 폰트 설정에서 별칭으로 줬던 "MalgunGothic"을 html 안에 폰트로 지정한다.
			String shtml1 = "<html><head></head><body style='font-family: MalgunGothic;'>"
					+ "<p>PDF 안에sdf 들어갈 내용입니다.</p>"
					+ "<div style='text-align:center; font-size:30px; background-color: red;'; ><h3>한글sdf, English, 漢字.</h3></div>"
					+ "</body></html>";

			String shtml2 = "<html>" +
					"<head></head>" +
					"<body>" +
					"<div>Hello world</div>" +
					"<div>명월입니다.</div>" +
					"</body>" +
					"</html>";

			xmlParser.parse(new StringReader(shtml1));
			document.close();
			writer.close();
		} catch (Exception e) {
			throw e;
		}

    }
}