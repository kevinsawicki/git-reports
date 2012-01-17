/*
 * Copyright (c) 2012 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package com.github.kevinsawicki.git.reports;

import static org.eclipse.jgit.revwalk.filter.RevFilter.NO_MERGES;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitFinder;
import org.gitective.core.CommitUtils;
import org.gitective.core.RepositoryUtils;
import org.gitective.core.filter.commit.AllCommitFilter;
import org.gitective.core.filter.commit.AllDiffEditFilter;
import org.gitective.core.filter.commit.AllDiffFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.AuthorSetFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitFileImpactFilter;
import org.gitective.core.filter.commit.CommitImpact;
import org.gitective.core.filter.commit.CommitLineImpactFilter;
import org.gitective.core.filter.commit.CommitterSetFilter;
import org.gitective.core.filter.commit.DiffFileCountFilter;
import org.gitective.core.filter.commit.DiffLineCountFilter;
import org.gitective.core.filter.commit.LastCommitFilter;
import org.gitective.core.stat.AuthorHistogramFilter;
import org.gitective.core.stat.CommitHistogram;
import org.gitective.core.stat.CommitterHistogramFilter;
import org.gitective.core.stat.UserCommitActivity;

/**
 * Report showing information for a repository's complete history
 */
public class TotalHistoryReport {

	private final Comparator<String> caseInsensitveComparator = new Comparator<String>() {

		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase(s2);
		}
	};

	private CommitHistogram authorHistogram;

	private CommitHistogram committerHistogram;

	private Map<String, Set<String>> namesToEmails = new HashMap<String, Set<String>>();

	private Set<String> authors = new TreeSet<String>(caseInsensitveComparator);

	private Set<String> committers = new TreeSet<String>(
			caseInsensitveComparator);

	private Set<String> files = new TreeSet<String>();

	private SortedSet<CommitImpact> mostFiles;

	private SortedSet<CommitImpact> mostLines;

	private RevCommit start;

	private RevCommit end;

	private long linesAdded;

	private long linesEdited;

	private long linesDeleted;

	private long added;

	private long modified;

	private long deleted;

	private long commits;

	private String projectName;

	private String projectVersion;

	private Linker linker;

	private Repository repository;

	/**
	 * @param linker
	 */
	public void setLinker(Linker linker) {
		this.linker = linker;
	}

	/**
	 * @return linker
	 */
	public Linker getLinker() {
		return linker;
	}

	/**
	 * @param projectName
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectVersion
	 */
	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}

	/**
	 * @return projectVersion
	 */
	public String getProjectVersion() {
		return projectVersion;
	}

	/**
	 * @return start
	 */
	public RevCommit getStart() {
		return start;
	}

	/**
	 * @return end
	 */
	public RevCommit getEnd() {
		return end;
	}

	/**
	 * @return added
	 */
	public long getAdded() {
		return added;
	}

	/**
	 * @return modified
	 */
	public long getModified() {
		return modified;
	}

	/**
	 * @return deleted
	 */
	public long getDeleted() {
		return deleted;
	}

	/**
	 * @return commits
	 */
	public long getCommits() {
		return commits;
	}

	/**
	 * @return authors
	 */
	public Set<String> getAuthors() {
		return authors;
	}

	/**
	 * @return committers
	 */
	public Set<String> getCommitters() {
		return committers;
	}

	/**
	 * @return files
	 */
	public Set<String> getFiles() {
		return files;
	}

	/**
	 * @return mostFiles
	 */
	public SortedSet<CommitImpact> getMostFiles() {
		return mostFiles;
	}

	/**
	 * @return mostLines
	 */
	public SortedSet<CommitImpact> getMostLines() {
		return mostLines;
	}

	/**
	 * @return diffs
	 */
	public long getDiffs() {
		return linesAdded + linesEdited + linesDeleted;
	}

	/**
	 * @return linesEdited
	 */
	public long getLinesEdited() {
		return linesEdited;
	}

	/**
	 * @return linesAdded
	 */
	public long getLinesAdded() {
		return linesAdded;
	}

	/**
	 * @return linesDeleted
	 */
	public long getLinesDeleted() {
		return linesDeleted;
	}

	/**
	 * Get number of commits authored by name
	 * 
	 * @param name
	 * @return commit count
	 */
	public int getAuthoredCommits(String name) {
		Set<String> emails = namesToEmails.get(name);
		if (emails == null)
			return 0;
		int count = 0;
		for (String email : emails) {
			UserCommitActivity activity = authorHistogram.getActivity(email);
			if (activity != null)
				count += activity.getCount();
		}
		return count;
	}

	/**
	 * Get number of commits committed by name
	 * 
	 * @param name
	 * @return commit count
	 */
	public int getCommittedCommits(String name) {
		Set<String> emails = namesToEmails.get(name);
		if (emails == null)
			return 0;
		int count = 0;
		for (String email : emails) {
			UserCommitActivity activity = committerHistogram.getActivity(email);
			if (activity != null)
				count += activity.getCount();
		}
		return count;
	}

	/**
	 * Get name for id
	 * 
	 * @param id
	 * @return name
	 */
	public String getCommitName(ObjectId id) {
		if (linker != null) {
			String url = linker.getCommitUrl(id.name());
			if (url != null)
				return "<a href=\"" + url + "\">" + id.name() + "</a>";
		}
		return id.name();
	}

	/**
	 * Get abbreviated name for id
	 * 
	 * @param id
	 * @return short name
	 */
	public String getCommitShortName(ObjectId id) {
		if (linker != null) {
			String url = linker.getCommitUrl(id.name());
			if (url != null)
				return "<a href=\"" + url + "\">" + id.abbreviate(7).name()
						+ "</a>";
		}
		return id.abbreviate(7).name();
	}

	/**
	 * Get compare link
	 * 
	 * @param label
	 * @return commits
	 */
	public String getCompare(String label) {
		if (linker != null) {
			String url = linker.getCompareUrl(end.name(), start.name());
			if (url != null)
				return "<a href=\"" + url + "\">" + label + "</a>";
		}
		return label;
	}

	/**
	 * Parse commit
	 * 
	 * @param id
	 * @return commit
	 */
	public RevCommit parseCommit(ObjectId id) {
		return CommitUtils.getCommit(repository, id);
	}

	private Map<String, Set<String>> mergeIdentities(
			Collection<PersonIdent> persons) {
		Map<String, Set<String>> namesToEmails = new TreeMap<String, Set<String>>(
				caseInsensitveComparator);
		namesToEmails.putAll(RepositoryUtils.mapNamesToEmails(persons));
		Map<String, Set<String>> emailsToNames = RepositoryUtils
				.mapEmailsToNames(persons);
		for (Entry<String, Set<String>> entry : emailsToNames.entrySet())
			for (String name : entry.getValue())
				namesToEmails.get(name).add(entry.getKey());

		Object[] nameEntries = namesToEmails.entrySet().toArray();
		for (int i = 0; i < nameEntries.length; i++) {
			@SuppressWarnings("unchecked")
			Entry<String, Set<String>> curr = (Entry<String, Set<String>>) nameEntries[i];
			if (curr == null)
				continue;
			for (int j = 0; j < nameEntries.length; j++) {
				if (i == j)
					continue;
				for (String email : curr.getValue().toArray(
						new String[curr.getValue().size()])) {
					@SuppressWarnings("unchecked")
					Entry<String, Set<String>> other = (Entry<String, Set<String>>) nameEntries[j];
					if (other == null)
						continue;
					if (other.getValue().contains(email)) {
						curr.getValue().addAll(other.getValue());
						namesToEmails.remove(other.getKey());
						nameEntries[j] = null;
					}
				}
			}
		}
		return namesToEmails;
	}

	/**
	 * Generate report for repository
	 * 
	 * @param repository
	 * @param start
	 * @throws IOException
	 */
	public void run(Repository repository, String start) throws IOException {
		this.repository = repository;
		this.start = CommitUtils.getCommit(repository, start);
		LastCommitFilter last = new LastCommitFilter();

		CommitFinder finder = new CommitFinder(repository);
		AuthorSetFilter authorsFilter = new AuthorSetFilter();
		CommitterSetFilter committersFilter = new CommitterSetFilter();
		CommitCountFilter countFilter = new CommitCountFilter();
		AuthorHistogramFilter authorHistogramFilter = new AuthorHistogramFilter();
		CommitterHistogramFilter committerHistogramFilter = new CommitterHistogramFilter();
		CommitLineImpactFilter lineImpactFilter = new CommitLineImpactFilter(
				100);
		CommitFileImpactFilter fileImpactFilter = new CommitFileImpactFilter(
				100);
		DiffLineCountFilter diffLineCountFilter = new DiffLineCountFilter();
		DiffFileCountFilter diffFileCountFilter = new DiffFileCountFilter();

		AllCommitFilter matcher = new AllCommitFilter();
		matcher.add(authorsFilter, committersFilter);
		matcher.add(authorHistogramFilter, committerHistogramFilter);
		matcher.add(new AllDiffFilter(true, diffFileCountFilter,
				fileImpactFilter, new AllDiffEditFilter(diffLineCountFilter,
						lineImpactFilter)));
		matcher.add(countFilter);
		if (last != null)
			matcher.add(last);

		finder.setMatcher(new AllCommitFilter(new AndCommitFilter(NO_MERGES,
				matcher)));
		finder.findFrom(start);
		this.end = last.getLast();

		mostFiles = fileImpactFilter.getCommits();
		mostLines = lineImpactFilter.getCommits();
		authorHistogram = authorHistogramFilter.getHistogram();
		committerHistogram = committerHistogramFilter.getHistogram();

		Map<String, Set<String>> authorNamesToEmails = mergeIdentities(authorsFilter
				.getPersons());
		namesToEmails.putAll(authorNamesToEmails);
		authors.addAll(authorNamesToEmails.keySet());

		Map<String, Set<String>> committerNamesToEmails = mergeIdentities(committersFilter
				.getPersons());
		namesToEmails.putAll(committerNamesToEmails);
		committers.addAll(committerNamesToEmails.keySet());

		commits = countFilter.getCount();

		added = diffFileCountFilter.getAdded();
		modified = diffFileCountFilter.getEdited();
		deleted = diffFileCountFilter.getDeleted();
		linesAdded = diffLineCountFilter.getAdded();
		linesEdited = diffLineCountFilter.getEdited();
		linesDeleted = diffLineCountFilter.getDeleted();
	}
}
