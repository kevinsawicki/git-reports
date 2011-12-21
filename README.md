# Git Reports

## Usage

### Release Report

The following snippet creates a report between two releases (1.1 & 1.2)

```java
ReleaseReport report = new ReleaseReport();
report.setProjectName("EGit");
report.setProjectVersion("1.2");

GitHubLinker linker = new GitHubLinker();
linker.setBase("https://github.com/eclipse/egit");
report.setLinker(linker);

String current = "remotes/origin/stable-1.2";
String previous = "remotes/origin/stable-1.1";

Repository repo = new FileRepository("/repos/egit/.git");
RevCommit base = CommitUtils.getBase(repo, current, previous);
report.run(repo, current, base.name());

Template tpl = Templates.getTemplate("release");
FileWriter writer = new FileWriter("/reports/egit.html");
tpl.process(report, writer);
```

## Example Generated Reports

* [JGit 1.2 Release](http://kevinsawicki.github.com/git-reports/jgit-1.2.html)
* [EGit 1.2 Release](http://kevinsawicki.github.com/git-reports/egit-1.2.html)

## Dependencies

* [JGit](https://github.com/eclipse/jgit)
* [gitective](https://github.com/kevinsawicki/gitective)
* [Twitter Bootstrap](https://github.com/twitter/bootstrap)
* [FreeMarker](http://freemarker.sourceforge.net/)

