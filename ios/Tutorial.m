//
//  Tutorial.m
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import "Tutorial.h"

@interface Tutorial ()

@end

@implementation Tutorial
const int totalTuts = 4; // total number of tutorials
UIImage *tuts[totalTuts];
int tutNum = 0; // current tutorial being viewed

- (IBAction)nextTut:(id)sender {
    tutNum++;
    
    // Dismiss Tutorial view if done
    if (tutNum >= totalTuts+1) {
        [self dismissViewControllerAnimated:YES completion:nil];
        tutNum = 0;
    }
    
    [tutView setImage:tuts[tutNum-1]]; // eventually fade this (TODO)
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Initialize tutorials
    tuts[0] = [UIImage imageNamed:@"tut1.png"];
    tuts[1] = [UIImage imageNamed:@"tut2.png"];
    tuts[2] = [UIImage imageNamed:@"tut3.png"];
    tuts[3] = [UIImage imageNamed:@"tut4.png"];
    
    // Start tutorial
    [self nextTut:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
