package com.library.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate
public class Templates {

  public static native TemplateInstance index();
  public static native TemplateInstance register();

  public static native TemplateInstance dashboard_admin();
  public static native TemplateInstance dashboard_user();

  public static native TemplateInstance emprestimos_admin();
  public static native TemplateInstance emprestimos_user();

  public static native TemplateInstance livros_admin();

  public static native TemplateInstance livros_user();

  public static native TemplateInstance usuarios_admin();

  public static native TemplateInstance not_found();

  public static native TemplateInstance unauthorized();
}
