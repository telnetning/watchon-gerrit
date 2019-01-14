package io.jenkins.plugins.sample;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {
    private String fileName;
    private String repoServerUrl;
    //private final ArrayList<HashMap<String, Map>> allRepos;
    private ArrayList<JSONObject> allRepos;

    @DataBoundConstructor
    public HelloWorldBuilder(String fileName, String repoServerUrl, ArrayList<JSONObject> allRepos)
    {
        this.allRepos = allRepos;
        this.repoServerUrl = repoServerUrl;
    }

    //写了 get 和 set 方法，打开 configure 时，配置才能显示出来
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRepoServerUrl() {
        return this.repoServerUrl;
    }

    public void setRepoServerUrl() {
        this.repoServerUrl = repoServerUrl;
    }

    public void setAllRepos(ArrayList<JSONObject> allRepos) {
        this.allRepos = allRepos;
    }

    public ArrayList<JSONObject> getAllRepos() {
        return this.allRepos;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Run previousSuccessulBuild = run.getPreviousSuccessfulBuild();
        Date date;
        if (previousSuccessulBuild != null) {
            date = previousSuccessulBuild.getTime();
        } else {
            date = run.getTime();
        }

        String lastBuildTime = df.format(date).replace(" ", "%20");
        //System.out.println(buildTime);

        for(JSONObject repo: this.allRepos) {
            String repoURL = this.repoServerUrl + "/changes/";
            String filter = "projects:" + repo.getString("repoName")
                            + "+branch:" + repo.getString("branchName")
                            + "+after:" + lastBuildTime;
            repoURL = repoURL + "?q=" + filter;
            System.out.println(repoURL);
            String response = new HttpClient().executeGet(repoURL);
            listener.getLogger().println(response);
        }
        /*
        listener.getLogger().println("Watch on: " + repoNames +":" + branchNames);
        */
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req,formData);
        }
    }
}
