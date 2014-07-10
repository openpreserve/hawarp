# Requires CSVDIFF (http://csvdiff.sourceforge.net)
# strip off header lines from ARC and WARC files in order to make them comparable line by line
cat %%cdx_arc%% | tail -n+2 > %%cdx_arc%%_stripped
cat %%cdx_warc%% | tail -n+3 > %%cdx_warc%%_stripped
# change to csvdiff directory
cd %%csvdiff_install_dir%%
# do not compare columns 9 (offset) and 10 (container file name), payload digest is compared!
# White space as column separator
perl csvdiff.pl -s " " -e %%cdx_arc%%_stripped -a %%cdx_warc%%_stripped -f "9 10"
