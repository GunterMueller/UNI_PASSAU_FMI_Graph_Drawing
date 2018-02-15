package org.visnacom.sugiyama.test;

import org.visnacom.model.*;
import org.visnacom.sugiyama.model.*;

public class TestGraphPresentation {
   
        //~ Instance fields ========================================================

        CompoundGraph c;
        Node n1;
        Node n2;
        Node n3;
        Node n4;
        Node n5;
        Node n6;
        Node n7;
        Node n8;
        Node n9;
        Node n10;
        Node n11;
        Node n12;
        Node n13;
        Node n14;
        Node n15;
        Node n16;
        Node n17;
        Node n18;
        Node n19;
        Node n20;
        Node n21;
        Node n22;
        Node n23;
        Node n24;
        Node n25;
        Node n26;
        Node n27;
        Node n28;
        Node n29;
        Node n30;
        Node n31;
        Node n32;
        Node n33;
        Node n34;
        Node n35;
        Node n36;
        Node n37;
        Node n38;
        Node n39;
        Node n40;
        Node n41;
        Node n42;
        Node n43;
        Node n44;
        Node n45;
        Edge e1;
        Edge e2;
        Edge e3;
        Edge e4;
        Edge e5;
        Edge e6;
        Edge e7;
        Edge e8;
        Edge e9;
        Edge e10;
        Edge e11;
        Edge e12;
        Edge e13;
        Edge e14;
        Edge e15;
        Edge e15b;
        Edge e16;
        Edge e17;
        Edge e18;
        Edge e19;
        Edge e20;
        Edge e21;
        Edge e22;
        Edge e23;
        Edge e24;
        Edge e25;
        private Node n47;
        private Node n46;
        private Edge e26;
        private Edge e27;
        private Node n48;
        private Edge e28;

        //~ Methods ================================================================

    
       

        /**
         *
         *
         * @param cpg DOCUMENT ME!
         */
        public void fillCompoundGraph(CompoundGraph cpg) {
            initialize(cpg);
        }

        /**
         *
         *
         * @param cpg DOCUMENT ME!
         */
        private void initialize(CompoundGraph cpg) {
            this.c = cpg;
            n1 = cpg.newLeaf(cpg.getRoot());
            n2 = cpg.newLeaf(n1);
            n3 = cpg.newLeaf(n2);
            n4 = cpg.newLeaf(n2);
            n5 = cpg.newLeaf(n1);
            n6 = cpg.newLeaf(cpg.getRoot());
            n7 = cpg.newLeaf(cpg.getRoot());
            n8 = cpg.newLeaf(n7);
            n9 = cpg.newLeaf(n7);
            n10 = cpg.newLeaf(cpg.getRoot());
            n11 = cpg.newLeaf(n10);
            n12 = cpg.newLeaf(n11);
            n13 = cpg.newLeaf(n11);
            n14 = cpg.newLeaf(n10);
            n15 = cpg.newLeaf(n10);
            n16 = cpg.newLeaf(n15);
            n17 = cpg.newLeaf(cpg.getRoot());
            n18 = cpg.newLeaf(n17);
            n19 = cpg.newLeaf(n17);
            n20 = cpg.newLeaf(n19);
            n21 = cpg.newLeaf(cpg.getRoot());
            n22 = cpg.newLeaf(n21);
            n23 = cpg.newLeaf(n21);
            n24 = cpg.newLeaf(cpg.getRoot());
            n25 = cpg.newLeaf(n24);
            n26 = cpg.newLeaf(n25);
            n27 = cpg.newLeaf(n25);
            n28 = cpg.newLeaf(n24);
            n29 = cpg.newLeaf(n24);
            n30 = cpg.newLeaf(n24);
            n31 = cpg.newLeaf(n30);
            n32 = cpg.newLeaf(n30);
            n33 = cpg.newLeaf(cpg.getRoot());
            n34 = cpg.newLeaf(n33);
            n35 = cpg.newLeaf(n33);
            n36 = cpg.newLeaf(n35);
            n37 = cpg.newLeaf(cpg.getRoot());
            n38 = cpg.newLeaf(n37);
            n39 = cpg.newLeaf(n37);
            n40 = cpg.newLeaf(n37);
            n41 = cpg.newLeaf(cpg.getRoot());
            n42 = cpg.newLeaf(n41);
            n43 = cpg.newLeaf(n42);
            n44 = cpg.newLeaf(n42);
            n45 = cpg.newLeaf(n41);
n46 = cpg.newLeaf(n33);
n47 = cpg.newLeaf(n33);
            n48 = cpg.newLeaf(n10);
            
            e1 = cpg.newEdge(n4, n6);
            e2 = cpg.newEdge(n7, n5);
            e3 = cpg.newEdge(n14, n12);
            e4 = cpg.newEdge(n11, n15);
            e5 = cpg.newEdge(n13, n14);
            e6 = cpg.newEdge(n16, n9);
            e7 = cpg.newEdge(n6, n7);
            e8 = cpg.newEdge(n6, n19);
            e9 = cpg.newEdge(n20, n18);
            e10 = cpg.newEdge(n20, n22);
            e11 = cpg.newEdge(n21, n7);
            e12 = cpg.newEdge(n26, n22);
            e13 = cpg.newEdge(n21, n25);
            e14 = cpg.newEdge(n26, n29);
            e15 = cpg.newEdge(n28, n26);
            e15b = cpg.newEdge(n28, n27);
            e16 = cpg.newEdge(n29, n23);
            e17 = cpg.newEdge(n31, n29);
            e18 = cpg.newEdge(n32, n29);
            e19 = cpg.newEdge(n22, n34);
            e20 = cpg.newEdge(n38, n22);
            e21 = cpg.newEdge(n43, n22);
            e22 = cpg.newEdge(n34, n38);
            e23 = cpg.newEdge(n44, n40);
            e24 = cpg.newEdge(n39, n45);
            e25 = cpg.newEdge(n36, n3);
            e26 = cpg.newEdge(n34, n46);
            e27 = cpg.newEdge(n46, n47);
            e28 = cpg.newEdge(n14, n48);
            
        }
    }
