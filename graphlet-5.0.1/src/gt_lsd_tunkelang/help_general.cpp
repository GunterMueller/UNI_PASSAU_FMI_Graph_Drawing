/* This software is distributed under the Lesser General Public License */
/* Functions needed but having nothing to do with xv or algorithm */

#include <GTL/GTL.h>
#include <stdio.h>
#include <iostream>

#ifndef __GTL_MSVCC
#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>

/*
 * commented out, Michael Forster, 30.01.99
 *
 * I think this is not needed any more and it gives Problems with
 * glibc2/libc5
 *
 */

#if 0
#if OSTYPE==AIX
#include <termios.h>
#include <sys/termio.h>
#else
#include <sys/termios.h>
#endif
#endif

#endif // __GTL_MSVCC

#include "glob_var_for_algo.h"

#include <graphed/error.h>

int a_to_b_like_x_to_d(int a, int b, int d)
{
    return( (int)(
	    ((float)a/(float)b)*(float)d
	    )
	    );
}

int signum(int argument)
{
    if      (argument>0) return( 1);
    else if (argument<0) return(-1);
    else                 return( 0);
}

int det(int x1, int y1, int x2, int y2, int x3, int y3)
{
    return(x1*(y2-y3)+x2*(y3-y1)+x3*(y1-y2));
}

void save_real_graphcoords(char *name)
{
    int i;
    FILE *file_ptr;
    struct nachfolger *pointer;
    file_ptr=fopen(name,"w");
    if(file_ptr==NULL) 
    {
	cout << "Cant open file in save_real_graphcoords" << endl;
	return;
    }

    
    fprintf(file_ptr,"%d\n",nodes);

    for(i=1;i<=nodes;i++)
    {
	pointer=real_adjacency_list[i];

	if(pointer==NULL) fprintf(file_ptr,"%d",0);

	else
        {
	    while(pointer!=NULL)
	    {
		fprintf(file_ptr,"%d ",pointer->nummer);
		pointer=pointer->next;
	    }

	    fprintf(file_ptr,"%d",-1);
	}

	fprintf(file_ptr,"\n");
    }

  
    fprintf(file_ptr,"\n");

    for(i=1;i<=nodes;i++)
    {
        fprintf(file_ptr,"%d "  ,real_x_coord[i]);
        fprintf(file_ptr,"%d   ",real_y_coord[i]);
    }

    fclose(file_ptr);

}

// void breakhandler(void)
// {
//     char c=0;
//     struct termios orgmode,curmode;   
//     while (c==0)
//     {
// 	/*
// 	fpr intf(stderr,"\n***********************************************************\n");
// 	fpri ntf(stderr,"! ! ! A T T E N T I O N ! ! !\nYou paused the algorithm.\nPress 'q' to return to GraphEd, other button to continue.\n");
// 	fprin tf(stderr,"\nTo reset CTRL-C to the normal behaviour,\njust press the quit-button in Tunkelang's algorithm.\n");
// 	fprin tf(stderr,"***********************************************************\n");
// 	*/
// 	fflush(stderr);

// 	ioctl(0,TCGETS,&orgmode ); /* Einlesen des OriginalModus              */
// 	ioctl(0,TCGETS,&curmode );
// 	curmode.c_iflag &= ~ ICRNL;/* Setzen der Eingabeparameter             */
// 	curmode.c_lflag &= ~ (ICANON | ECHO);
// 	curmode.c_cc[VTIME] = 0;
// 	curmode.c_cc[VMIN] = 1;
// 	ioctl(0,TCSETS,&curmode);  /* Setzen des Eingabemodus                 */
// 	(void) read(0,&c,1);       /* Lesen eines Zeichens                    */
// 	ioctl(0,TCSETS,&orgmode);  /* Zuruecksetzen des urspruenglichen Modus */

// 	/* 
// 	   fputc(c,stderr); 
// 	   */
// 	if (c != 13) fputc('\n',stderr);
// 	fflush(stderr);
      
// 	switch(c)
// 	{
// 	    case 'q':
// 	    case 'Q':  /*fpr intf(stderr,"Returning to GraphEd ...\n"); fflush(stderr);*/
// 		quit_the_algorithm=1; 
// 		return;
// 	    default :
// 		/*fpri ntf(stderr,"Continuing algorithm ...\n"); fflush(stderr);*/
// 		return;
// 	}
//     }
// }
