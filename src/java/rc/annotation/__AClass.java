/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Mitasoa
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface __AClass {
    public String tableName() default "";
}
