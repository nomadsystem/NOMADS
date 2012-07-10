//
//  main.m
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/10/12.
//  Copyright University of Oregon 2012. All rights reserved.
//

#import <UIKit/UIKit.h>

int main(int argc, char *argv[]) {
    
    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
    int retVal = UIApplicationMain(argc, argv, nil, @"BindleAppDelegate");
    [pool release];
    return retVal;
}
