package top.ticho.boot.swagger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.Parameter;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 重写 将Document转换成Swagger 类, 根据order进行排序 和 ExpandedParameterBuilderPlugin配套使用
 * Primary 同一个接口，可能会有几种不同的实现类，而默认只会采取其中一种的情况下
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 * @see BaseSwaggerParameterBuilder
 */
@ConditionalOnBean(BaseSwaggerConfig.class)
@Primary
@Component("ServiceModelToSwagger2Mapper")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BaseModelToSwaggerMapper extends ServiceModelToSwagger2MapperImpl {

    @Override
    protected List<io.swagger.models.parameters.Parameter> parameterListToParameterList(List<Parameter> list) {
        // list需要根据order|postion排序
        list = list.stream().sorted(Comparator.comparingInt(Parameter::getOrder)).collect(Collectors.toList());
        return super.parameterListToParameterList(list);
    }
}
