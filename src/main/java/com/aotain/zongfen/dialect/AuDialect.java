package com.aotain.zongfen.dialect;

import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 20180511
 * @author chenzr
 */

@Component
public class AuDialect extends AbstractProcessorDialect{

    private static final String DIALECT_NAME = "au";
    private static final String PREFIX = "th";
    public static final int PROCESSOR_PRECEDENCE = 1000;



    protected AuDialect() {
        super(DIALECT_NAME, PREFIX, PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors( String s ) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new AuElementProcessor(s));
        return processors;
    }


}
