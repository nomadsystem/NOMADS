//
//  NGlobals.h
//
//  Initial version, DJT on 6/22/12.
//

#import <Foundation/Foundation.h>

@interface NGlobals : NSObject
{
  static int clientDebugLevel;
  static int serverDebugLevel;
  static int libraryDebugLevel;
  static NSString    serverName;
  static int serverPort;
  static int serverPortDT;
  static int serverPortSK;
  static int serverPortPT;
  static int serverPortMB;
}

@end


