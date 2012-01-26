<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="bootstrap.min.css">
<link rel="stylesheet" href="core.css">
<script src="jquery.js"></script>
<script src="bootstrap-tabs.js"></script>
</head>
<title>${projectName} ${projectVersion}</title>
<body>

	<div class="container">
		<div class="page-header">
			<h1>
				${projectName} ${projectVersion} <small>
					${end.authorIdent.when?string("EEEEE, MMMMM d yyyy")} -
					${start.authorIdent.when?string("EEEEE, MMMMM d yyyy")}</small>
			</h1>
		</div>
		<div class="row">
			<div class="span12">
				<h3>Quick Facts</h3>
				<ul>
					<li>${commits} commits</li>
					<li>${merges} merge commits</li>
					<li>${mergeConflicts?size} merges with content changes</li>
					<li>${authors?size} authors</li>
					<li>${committers?size} committers</li>
					<li>${linesAdded} lines added</li>
					<li>${linesEdited} lines changed</li>
					<li>${linesDeleted} lines removed</li>
					<li>${added} files added</li>
					<li>${modified} files edited</li>
					<li>${deleted} files removed</li>
					<li>First commit was <code>${getCommitName(end)}</code> by
						${end.authorIdent.name}
					</li>
					<li>Latest commit is <code>${getCommitName(start)}</code> by
						${start.authorIdent.name}
					</li>
				</ul>
			</div>
		</div>
		<div class="page-header" id="contributors">
			<h1>
				Contributors <small>who made the changes</small>
			</h1>
		</div>
		<div class="row">
			<div class="span8">
				<h3>
					Authors <small>${authors?size}</small>
				</h3>
				<ol>
					<#list authors as author>
					<li>${author}&nbsp;&nbsp;<span class="label success">${getAuthoredCommits(author)}</span></li>
					</#list>
				</ol>
			</div>
			<div class="span8">
				<h3>
					Committers <small>${committers?size}</small>
				</h3>
				<ol>
					<#list committers as committer>
					<li>${committer}&nbsp;&nbsp;<span class="label success">${getCommittedCommits(committer)}</span></li>
					</#list>
				</ol>
			</div>
		</div>
		<div class="row">
			<div class="span8">
				<h3>
					Author Line Impact <small>lines touched</small>
				</h3>
				<ol>
					<#list authorLineImpacts as author>
					<#assign impact = getAuthorLineImpact(author)>
					<li>${author}&nbsp;&nbsp;<span class="label success">+${impact.add}</span>
						<span class="label warning">${impact.edit}</span> <span
						class="label important">-${impact.delete}</span></li>
				    </#list>
				</ol>
			</div>
			<div class="span8">
				<h3>
					Author File Impact <small>files touched</small>
				</h3>
				<ol>
					<#list authorFileImpacts as author>
					<#assign impact = getAuthorFileImpact(author)>
					<li>${author}&nbsp;&nbsp;<span class="label success">+${impact.add}</span>
						<span class="label warning">${impact.edit}</span> <span
						class="label important">-${impact.delete}</span></li>
				    </#list>
				</ol>
			</div>
		</div>
		<div class="page-header" id="commits">
			<h1>
				Commits <small>biggest changes made</small>
			</h1>
		</div>
		<div class="row">
			<div class="span8">
				<h3>Most Lines Changed</h3>
				<ol>
					<#list mostLines as impact>
					<li><code>${getCommitShortName(impact.commit)}</code> by
						${parseCommit(impact.commit).authorIdent.name}&nbsp;&nbsp;<span
						class="label success">+${impact.add}</span> <span
						class="label warning">${impact.edit}</span> <span
						class="label important">-${impact.delete}</span></li> </#list>
				</ol>
			</div>
			<div class="span8">
				<h3>Most Files Changed</h3>
				<ol>
					<#list mostFiles as impact>
					<li><code>${getCommitShortName(impact.commit)}</code> by
						${parseCommit(impact.commit).authorIdent.name}&nbsp;&nbsp;<span
						class="label success">+${impact.add}</span> <span
						class="label warning">${impact.edit}</span> <span
						class="label important">-${impact.delete}</span></li></#list>
				</ol>
			</div>
		</div>
		<div class="page-header" id="commits">
			<h1>
				Danger Zone
			</h1>
		</div>
		<div class="row">
			<div class="span8">
				<h3>
					Merge Commits With Content Changes <small>(${mergeConflicts?size})</small>
				</h3>
				<ul>
					<#list mergeConflicts as impact>
					<li><code>${getCommitShortName(impact)}</code> by
						${parseCommit(impact).authorIdent.name}</li> </#list>
				</ul>
			</div>
			<div class="span8">
				<h3>
					Commits With Duplicate Blobs <small>(${dupeCommits?size})</small>
				</h3>
				<ul>
					<#list dupeCommits as impact>
					<li><code>${getCommitShortName(impact)}</code> by
						${parseCommit(impact).authorIdent.name} (${getDupeCount(impact)})</li>
					</#list>
				</ul>
			</div>
		</div>
	</div>
</body>

</html>