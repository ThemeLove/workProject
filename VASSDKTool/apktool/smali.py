import os
import os.path
import re
import platform
import subprocess
import inspect
import sys
import codecs
import threading
import time
import shutil
def get_smali_method_count(smaliFile, allMethods):

    if not os.path.exists(smaliFile):
        return 0

    f = open(smaliFile)
    lines = f.readlines()
    f.close()

    classLine = lines[0]
    classLine.strip()
    if not classLine.startswith(".class"):
        return 0

    className = parse_class(classLine)

    count = 0
    for line in lines:
        line = line.strip()

        method = None
        if line.startswith(".method"):
            method = parse_method_default(className, line)
        elif line.startswith("invoke-"):
            method = parse_method_invoke(line)

        if method is None:
            continue
        if method not in allMethods:
            count = count + 1
            allMethods.append(method)
        else:
            pass

    return count

def parse_class(line):
    if not line.startswith(".class"):
        return None
    blocks = line.split()
    return blocks[len(blocks)-1]


def parse_method_default(className, line):
    if not line.startswith(".method"):
        return None
    blocks = line.split()
    return className + "->" + blocks[len(blocks)-1]


def parse_method_invoke(line):
    if not line.startswith("invoke-"):
        return None
    blocks = line.split()
    return blocks[len(blocks)-1]

def copy_file(from_file,target_file):
    shutil.copy(from_file, target_file)

def del_file(filename):
    os.remove(filename)

def list_files(filepath):
    files=[]
    for dirpath, dirnames, filenames in os.walk(filepath):
        for filename in filenames:
            file=dirpath + "/" + filename
            if file.endswith(".smali"):
                files.append(file)
    return files
#copy_file('D:/work/python/study-python/apk/a/1/1.txt', 'D:/work/python/study-python/apk/a/2/2.txt')
#del_file('D:/work/python/study-python/apk/a/1/1.txt')
#path = os.path.split('D:/work/python/study-python/apk/a/1/2.txt')
#print path[0]


maxFuncNum = 65000
currFucNum = 0
totalFucNum = 0
allRefs = []
#currentPath = os.getcwd()
currentPath = "D:/workspace_eclipse_javaee/VASSDKTool/work/temp"
allFiles=list_files(currentPath)
smaliPath = currentPath + '/smali'
currDexIndex = 0

for f in allFiles:
    f = f.replace("\\", "/")
    if "/com/vas/vassdk" in f or "/android/support/multidex" in f:
        currFucNum = currFucNum + get_smali_method_count(f, allRefs)

totalFucNum = currFucNum
print "vassdk+multidex =", totalFucNum
print "file nums =",len(allFiles)
for f in allFiles:
    f = f.replace("\\", "/")

    if not f.endswith(".smali"):
        continue

    if "/com/vas/vassdk" in f or "/android/support/multidex" in f:
        continue

    thisFucNum = get_smali_method_count(f, allRefs)
    totalFucNum = totalFucNum + thisFucNum
    print 'FileName => ',f,' #total => ',totalFucNum,' #This file num => ',thisFucNum
    if totalFucNum >= maxFuncNum:
        totalFucNum=0
        currDexIndex = currDexIndex + 1

    if currDexIndex >= 1:
        newDexPath = currentPath + "/smali_classes" + str(currDexIndex)
        targetPath = newDexPath + f[len(smaliPath):]
        targetPath = targetPath.replace("\\","/");
        dirs = os.path.split(targetPath)
        print dirs[0]
        if not os.path.exists(dirs[0]) :
            os.makedirs(dirs[0])

        print f,' => copy to targetPath =>',targetPath
        copy_file(f, targetPath)
        print 'delete file => %s' % f
        del_file(f)

print "the total func num:" + str(totalFucNum)
print "split dex success. the classes.dex num:" + str(currDexIndex)
