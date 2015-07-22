package org.ortolan.plugins;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "informacoesSuperPom", defaultPhase = LifecyclePhase.INSTALL)
public class InformacoesSuperpom extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    private BufferedWriter writer;
    private String fileName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        fileName = String.format("%s-%s-all-dependencies.html",
                project.getArtifactId(),
                project.getVersion());

        List<MavenProject> projects = new ArrayList<MavenProject>();

        MavenProject currentProject = project;

        while(currentProject.hasParent()) {
            projects.add(currentProject);
            currentProject = currentProject.getParent();
        }
        projects.add(currentProject);

        try {
            openHtml();
            beginHTML();
            createIndex(projects);

            for(MavenProject mavenProject : projects) {
                List<Dependency> dependencies = mavenProject.getDependencies();
                dependencies.addAll(mavenProject.getDependencyManagement().getDependencies());

                beginTable(mavenProject.getName());

                if(dependencies != null) {
                    for (Dependency dependency : dependencies) {
                        addDependency(dependency);
                    }
                }

                endTable();
            }

            endHTML();
            closeHtml();

        } catch (IOException e) {
            getLog().error(e);
        }
    }

    private void openHtml() throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName));
    }

    private void closeHtml() throws IOException {
        writer.close();
    }

    private void beginHTML() throws IOException {
        writer.write("<!DOCTYPE HTML>");
        writer.newLine();
        writer.write("<html>");
        writer.newLine();
        writer.write(" <head>");
        writer.newLine();
        writer.write("   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        writer.newLine();
        writer.write("   <title>Dependencies for project " + project.getName() + "</title>");
        writer.newLine();
        writer.write(" </head>");
        writer.newLine();
        writer.write(" <body>");
        writer.newLine();
        writer.write("   <h1>Dependencies for project " + project.getName() + "</h1>");
        writer.newLine();
    }

    private void createIndex(List<MavenProject> projects) throws IOException {
        writer.write("   <ul>");
        writer.newLine();

        for(MavenProject project : projects) {
            writer.write(String.format("    <li><a href=\"#%s\">%s</a></li>", project.getName(), project.getName()));
            writer.newLine();
        }

        writer.write("   </ul>");
        writer.newLine();
    }

    private void beginTable(String project) throws IOException {
        writer.write(String.format("   <h2><a name=\"%s\">%s</a></h2>", project, project));
        writer.write("   <table>");
        writer.newLine();
        writer.write("    <tr>" +
                "<td><strong>GroupId</strong></td>" +
                "<td><strong>ArtifactId</strong></td>" +
                "<td><strong>Version</strong></td>" +
                "<td><strong>Scope</strong></td></tr>");
        writer.newLine();
    }

    private void addDependency(Dependency dependency) throws IOException {
        writer.write(
                String.format("    <tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                        dependency.getArtifactId(),
                        dependency.getGroupId(),
                        dependency.getVersion(),
                        (dependency.getScope() != null)?dependency.getScope():"compile"));
        writer.newLine();
    }

    private void endTable() throws IOException {
        writer.write("   </table>");
        writer.newLine();
    }

    private void endHTML() throws IOException {
        writer.write(" </body>");
        writer.newLine();
        writer.write("</html>");
    }
}
