/*
 * Copyright (c) 2013 Chris Newland. All rights reserved.
 * Licensed under https://github.com/chriswhocodes/jitwatch/blob/master/LICENSE-BSD
 * http://www.chrisnewland.com/jitwatch
 */
package com.chrisnewland.jitwatch.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.chrisnewland.jitwatch.util.StringUtil;

public class JITWatchConfig
{
    private static final String PROPERTIES_FILENAME = "jitwatch.properties";

    private static final String KEY_ALLOWED_PACKAGES = "PackageFilter";
    private static final String KEY_SOURCE_LOCATIONS = "Sources";
    private static final String KEY_CLASS_LOCATIONS = "Classes";
    private static final String KEY_SHOW_JIT_ONLY_MEMBERS = "JitOnly";
    private static final String KEY_SHOW_JIT_ONLY_CLASSES = "JitOnlyClasses";
    private static final String KEY_SHOW_HIDE_INTERFACES = "HideInterfaces";
    private static final String KEY_SHOW_NOTHING_MOUNTED = "ShowNothingMounted";

    private IJITListener logListener;

    private List<String> allowedPackages = new ArrayList<>();
    private List<String> sourceLocations = new ArrayList<>();
    private List<String> classLocations = new ArrayList<>();

    private boolean showOnlyCompiledMembers = true;
    private boolean showOnlyCompiledClasses = false;
    private boolean hideInterfaces = true;
    private boolean showNothingMounted = true;

    public JITWatchConfig(IJITListener logListener)
    {
        this.logListener = logListener;
        loadConfig();
    }

    private void loadConfig()
    {
        Properties loadProps = new Properties();

        try (FileReader fr = new FileReader(getConfigFile()))
        {
            loadProps.load(fr);
        }
        catch (FileNotFoundException fnf)
        {

        }
        catch (IOException ioe)
        {
            logListener.handleErrorEntry(ioe.toString());
        }

        String confPackages = loadProps.getProperty(KEY_ALLOWED_PACKAGES);
        String confClasses = loadProps.getProperty(KEY_CLASS_LOCATIONS);
        String confSources = loadProps.getProperty(KEY_SOURCE_LOCATIONS);

        if (confPackages != null && confPackages.trim().length() > 0)
        {
            allowedPackages = StringUtil.textToList(confPackages, ",");
        }

        if (confClasses != null && confClasses.trim().length() > 0)
        {
            classLocations = StringUtil.textToList(confClasses, ",");
        }

        if (confSources != null && confSources.trim().length() > 0)
        {
            sourceLocations = StringUtil.textToList(confSources, ",");
        }

        showOnlyCompiledMembers = Boolean.parseBoolean(loadProps.getProperty(KEY_SHOW_JIT_ONLY_MEMBERS, Boolean.TRUE.toString()));
        showOnlyCompiledClasses = Boolean.parseBoolean(loadProps.getProperty(KEY_SHOW_JIT_ONLY_CLASSES, Boolean.FALSE.toString()));
        hideInterfaces = Boolean.parseBoolean(loadProps.getProperty(KEY_SHOW_HIDE_INTERFACES, Boolean.TRUE.toString()));
        showNothingMounted = Boolean.parseBoolean(loadProps.getProperty(KEY_SHOW_NOTHING_MOUNTED, Boolean.TRUE.toString()));    
    }

    public void saveConfig()
    {
        Properties saveProps = new Properties();

        saveProps.put(KEY_ALLOWED_PACKAGES, StringUtil.listToText(allowedPackages, ","));
        saveProps.put(KEY_SOURCE_LOCATIONS, StringUtil.listToText(sourceLocations, ","));
        saveProps.put(KEY_CLASS_LOCATIONS, StringUtil.listToText(classLocations, ","));
        saveProps.put(KEY_SHOW_JIT_ONLY_MEMBERS, Boolean.toString(showOnlyCompiledMembers));
        saveProps.put(KEY_SHOW_JIT_ONLY_CLASSES, Boolean.toString(showOnlyCompiledClasses));
        saveProps.put(KEY_SHOW_HIDE_INTERFACES, Boolean.toString(hideInterfaces));
        saveProps.put(KEY_SHOW_NOTHING_MOUNTED, Boolean.toString(showNothingMounted));
        
        try (FileWriter fw = new FileWriter(getConfigFile()))
        {
            saveProps.store(fw, null);
        }
        catch (IOException ioe)
        {
            logListener.handleErrorEntry(ioe.toString());
        }
    }

    private File getConfigFile()
    {
        return new File(System.getProperty("user.dir"), PROPERTIES_FILENAME);
    }

    public boolean isAllowedPackage(String fqMethodName)
    {
        return allowedPackages.size() == 0 || checkPackage(fqMethodName);
    }

    private boolean checkPackage(String packageName)
    {
        for (String allowedPackage : allowedPackages)
        {
            if (allowedPackage.equals(packageName) || packageName.startsWith(allowedPackage + "."))
            {
                return true;
            }
        }

        return false;
    }

    public List<String> getAllowedPackages()
    {
        return Collections.unmodifiableList(allowedPackages);
    }

    public List<String> getClassLocations()
    {
        return Collections.unmodifiableList(classLocations);
    }

    public List<String> getSourceLocations()
    {
        return Collections.unmodifiableList(sourceLocations);
    }

    public void setAllowedPackages(List<String> allowedPackages)
    {
        this.allowedPackages = allowedPackages;
    }

    public void setSourceLocations(List<String> sourceLocations)
    {
        this.sourceLocations = sourceLocations;
    }

    public void setClassLocations(List<String> classLocations)
    {
        this.classLocations = classLocations;
    }

    public boolean isShowOnlyCompiledMembers()
    {
        return showOnlyCompiledMembers;
    }

    public void setShowOnlyCompiledMembers(boolean showOnlyCompiledMembers)
    {
        this.showOnlyCompiledMembers = showOnlyCompiledMembers;
    }
    
    public boolean isShowOnlyCompiledClasses()
    {
        return showOnlyCompiledClasses;
    }

    public void setShowOnlyCompiledClasses(boolean showOnlyCompiledClasses)
    {
        this.showOnlyCompiledClasses = showOnlyCompiledClasses;
    }

    public boolean isHideInterfaces()
    {
        return hideInterfaces;
    }

    public void setHideInterfaces(boolean hideInterfaces)
    {
        this.hideInterfaces = hideInterfaces;
    }
    
    public boolean isShowNothingMounted()
    {
    	return showNothingMounted;
    }
    
    public void setShowNothingMounted(boolean showNothingMounted)
    {
    	this.showNothingMounted = showNothingMounted;
    }
}