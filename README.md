GDSC ImageJ Core
================

This package contains core code used by the GDSC ImageJ and GDSC SMLM ImageJ 
plugins. It is a dependency for both of those packages.


Maven Installation
------------------

This package is used by other GDSC packages. It is only necessary to perform an 
install if you are building the other packages from the source code. 

1. Clone the repository

        git clone https://github.com/aherbert/GDSC-Core.git

2. Build the code and install using Maven

        cd GDSC-Core
        mvn install

This will produce a gdsc-core-[VERSION].jar file in the local Maven repository. 
You can now build the other GDSC packages that depend on this code.


Legal
-----

See [LICENSE](LICENSE)


# About #

###### Repository name ######
GDSC ImageJ Core

###### Owner(s) ######
Alex Herbert

###### Institution ######
Genome Damage and Stability Centre, University of Sussex

###### URL ######
http://www.sussex.ac.uk/gdsc/intranet/microscopy/imagej/gdsc_plugins

###### Email ######
a.herbert@sussex.ac.uk

###### Description ######
The Genome Damage and Stability Centre (GDSC) ImageJ core package contains 
common code used by the GDSC ImageJ and GDSC SMLM ImageJ plugins.
