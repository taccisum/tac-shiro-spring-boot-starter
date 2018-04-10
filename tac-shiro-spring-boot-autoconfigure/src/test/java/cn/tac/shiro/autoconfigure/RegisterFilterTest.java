package cn.tac.shiro.autoconfigure;

import cn.tac.shiro.support.config.ShiroProperties;
import cn.tac.shiro.support.config.filter.AjaxUserFilter;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.Filter;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tac
 * @since 29/03/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RegisterFilterTest.class)
@ImportAutoConfiguration(ShiroAutoConfiguration.class)
@ActiveProfiles("register-test")
public class RegisterFilterTest {
    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    @Test
    public void testSimply() {
        assertThat(shiroFilterFactoryBean).isNotNull();
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        assertThat(filters).isNotEmpty();

        Filter filter = filters.get("test");
        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(TestFilter.class);

        Filter filter1 = filters.get("test1");
        assertThat(filter1).isNotNull();
        assertThat(filter1).isInstanceOf(TestFilterWithProperties.class);
    }

    public static class TestFilter extends AjaxUserFilter {
        @Override
        protected String responseBody() {
            return "test";
        }
    }

    public static class TestFilterWithProperties extends AjaxUserFilter {
        private ShiroProperties shiroProperties;

        public TestFilterWithProperties(ShiroProperties shiroProperties) {
            Objects.requireNonNull(shiroProperties, "shiro properties");
            this.shiroProperties = shiroProperties;
        }

        @Override
        protected String responseBody() {
            return "test";
        }
    }
}
