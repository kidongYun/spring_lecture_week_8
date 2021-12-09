package com.artineer.spring_lecture_week_2.apiversion;

import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiVersionRequestCondition extends AbstractRequestCondition<ApiVersionRequestCondition> {
    private final Set<VersionRange> versions;

    public ApiVersionRequestCondition(int from, int to) {
        this(versionRange(from, to));
    }

    public ApiVersionRequestCondition(Collection<VersionRange> versions) {
        this.versions = Set.copyOf(versions);
    }

    private static Set<VersionRange> versionRange(int from, int to) {
        HashSet<VersionRange> versionRanges = new HashSet<>();

        if(from > 0) {
            int toVersion = (to > 1) ? to : Version.MAX_VERSION;
            VersionRange versionRange = new VersionRange(from, toVersion);

            versionRanges.add(versionRange);
        }

        return versionRanges;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        Set<VersionRange> newVersions = new LinkedHashSet<>(this.versions);
        newVersions.addAll(other.versions);

        return new ApiVersionRequestCondition(newVersions);
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        String accept = request.getRequestURI();

        Pattern regexPattern = Pattern.compile("(\\/api\\/v)(\\d+)(\\/).*");

        Matcher matcher = regexPattern.matcher(accept);
        if(matcher.matches()) {
            int version = Integer.parseInt(matcher.group(2));

            for(VersionRange versionRange : versions) {
                if(versionRange.includes(version)) {
                    return this;
                }
            }
        }

        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        if(versions.size() == 1 && other.versions.size() == 1) {
            return versions.stream().findFirst().get().compareTo(other.versions.stream().findFirst().get()) * -1;
        }

        return 0;
    }

    @Override
    protected Collection<?> getContent() {
        return versions;
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }
}
