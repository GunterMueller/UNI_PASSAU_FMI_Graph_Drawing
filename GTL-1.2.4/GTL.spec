### BEGIN GTL.spec
# Note that this is NOT a relocatable package

Name: GTL
Summary: The Graph Template Library
Version: 1.0.0
Release: 1
Group: Development/Libraries
Source: http://infosun.fmi.uni-passau.de/GTL/download/GTL-1.0.0.tar.gz
URL: http://infosun.fmi.uni-passau.de/GTL/
Copyright: Copyright (C) 1999 University of Passau
Vendor: University of Passau
Packager: Marcus Raitner <raitner@fmi.uni-passau.de>
Prefix: /usr
BuildRoot: /var/tmp/%{name}-%{version}-root

%description

There are some basic data structures and algorithms that are frequently
used in many programs. This includes container classes such as vectors,
sets and lists.

Many commercial but some free libraries have implemented these data
structures and algorithms in a general way. This makes them usable for
many purposes.

One of these libraries is the Standard Template Library (STL), an
extremely flexible implementation of many container classes and standard
algorithms. STL is supposed to become a part of the C++ standard library
and therefore is an ideal basis when writing portable programs.

Unfortunately, STL has no support for graphs and graph
algorithms. However, graphs are widely used for complex relational
structures.

Since we are intensively working with graph algorithms and Graphlet, we
decided to implement GTL, a graph library based on STL. For the design
of GTL's API the API of LEDA has served as a basis. GTL contains the
classes needed to work with graphs, nodes and edges and some basic
algorithms as building blocks for more complex graph algorithms. Further
algorithms are under work.'

%changelog
* Thu Oct 26 2000 Marcus Raitner <raitner@fmi.uni-passau.de>
- New release
* Tue Mar 07 2000 Marcus Raitner <raitner@fmi.uni-passau.de>
- New release
* Wed Feb 02 2000 Reinhard Katzmann <reinhard@suamor.de>
- Added cleanup section for temporary BuildRoot
- Simplified file section
- Needed to fix GTL.h to use map even with gcc 2.95 :-( (patch file)
- Added Buildroot which was missing and would overwrite old version of GTL while installing
* Fri Nov 26 1999 Marcus Raitner <raitner@fmi.uni-passau.de>
- Initial RPM packaging.

%prep
%setup
#%patch -p0

%build
CXXFLAGS=-O3 CPPFLAGS="" CFLAGS="" ./configure --enable-static --prefix=/usr 
make

%install
make prefix=$RPM_BUILD_ROOT%{prefix} install
strip $RPM_BUILD_ROOT%{prefix}/lib/libGTL-1.0.0.so $RPM_BUILD_ROOT%{prefix}/lib/libGTL.a

%clean
rm -rf $RPM_BUILD_ROOT

%post
PATH="$PATH:/sbin" ldconfig -n /usr/lib

%files
%doc README  
%doc INSTALL
%doc doc/latex/GTL.ps
%doc doc/html
/usr/include/GTL
/usr/lib
###END GTL.spec
