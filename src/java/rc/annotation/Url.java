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
public @interface Url {
    public String url() default "NaN";
    public boolean isAuthorised() default true;
    public String access() default "null";
    public String redirectFalse() default "NaN";
    public boolean isSessionDestroy() default false;
    public String sessionDestroy() default "null";
    public boolean isRemoveAllSession() default false;
}
