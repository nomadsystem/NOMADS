//
//  NGlobals.h
//
//  Initial version, DJT on 6/22/12.
//

#import <Foundation/Foundation.h>

@interface NGlobals : NSObject
{
  @public int clientDebugLevel;
  @public int serverDebugLevel;
  @public int libraryDebugLevel;
  @public NSString    *serverName;
  @public int serverPort;
  @public int serverPortDT;
  @public int serverPortSK;
  @public int serverPortPT;
  @public int serverPortMB;
}

@end


