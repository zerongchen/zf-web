package com.aotain.zongfen.dialect;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @since 20180511
 * @author chenzr
 */
public class AuElementProcessor extends AbstractAttributeTagProcessor {

    private static final String ATTR_NAME = "authorized";
//    private static final String ELE_NAME = "div";
    private static final int PRECEDENCE = 10000;

    protected AuElementProcessor( String dialectPrefix ) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess( ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String s, IElementTagStructureHandler iElementTagStructureHandler ) {

        HttpSession httpSession = ((WebEngineContext) iTemplateContext).getSession();
        HttpServletRequest request = ((WebEngineContext) iTemplateContext).getRequest();
        String attributes = null;

        if(s.indexOf("${")==-1){
            attributes=s;
        }else {
            String attributesName=s.substring(s.indexOf("${")+2,s.lastIndexOf("}"));
            attributes = request.getAttribute(attributesName)+s.substring(s.indexOf("_"),s.length());
        }
        if(httpSession.getAttribute(attributes)!=null && String.valueOf(httpSession.getAttribute(attributes)).equalsIgnoreCase("1")){
           //do nothing

        }else {
            iElementTagStructureHandler.removeElement();
        }
    }
}
