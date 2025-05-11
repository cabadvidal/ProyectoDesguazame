/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.util;

import com.desguazame.desguazame_escritorio.model.Sockets;
import com.desguazame.desguazame_escritorio.model.User;

/**
 *
 * @author Charlie
 */
public class AppGlobals {
    public static User  user;
    public static String token;
    public static Sockets socket = new Sockets("http://192.168.1.122:10010");
}
