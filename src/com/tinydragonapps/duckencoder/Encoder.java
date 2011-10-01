package com.tinydragonapps.duckencoder;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ //import javax.swing.text.BadLocationException;
/*     */ //import javax.swing.text.Document;
/*     */ //import javax.swing.text.rtf.RTFEditorKit;
/*     */ 
/*     */ public class Encoder
/*     */ {
/*     */ 
/*     */   static void encodeToFile(String inStr, String fileDest) {
/* 123 */     String[] instructions = inStr.split("\n");
/* 124 */     List file = new ArrayList();
/* 125 */     int defaultDelay = 0;
/*     */ 
/* 127 */     for (int i = 0; i < instructions.length; i++) {
/*     */       try {
/* 129 */         boolean delayOverride = false;
/* 130 */         String[] instruction = instructions[i].split(" ", 2);
/*     */ 
/* 132 */         if ((instruction[0].equals("DEFAULT_DELAY")) || 
/* 133 */           (instruction[0].equals("DEFAULTDELAY"))) {
/* 134 */           defaultDelay = (byte)Integer.parseInt(instruction[1]);
/* 135 */         } else if (instruction[0].equals("DELAY")) {
/* 136 */           int delay = Integer.parseInt(instruction[1]);
/* 137 */           while (delay > 0) {
/* 138 */             file.add(Byte.valueOf((byte)0));
/* 139 */             if (delay > 255) {
/* 140 */               file.add(Byte.valueOf((byte)-1));
/* 141 */               delay -= 255;
/*     */             } else {
/* 143 */               file.add(Byte.valueOf((byte)delay));
/* 144 */               delay = 0;
/*     */             }
/*     */           }
/* 147 */           delayOverride = true;
/* 148 */         } else if (instruction[0].equals("STRING")) {
/* 149 */           for (int j = 0; j < instruction[1].length(); j++) {
/* 150 */             char c = instruction[1].charAt(j);
/* 151 */             file.add(Byte.valueOf(charToByte(c)));
/*     */ 
/* 154 */             byte shiftByte = 0;
/* 155 */             if ((c >= 'A') && (c <= 'Z'))
/*     */             {
/* 157 */               shiftByte = 2;
/*     */             }
/* 159 */             else switch (c)
/*     */               {
/*     */               case '!':
/*     */               case '"':
/*     */               case '#':
/*     */               case '$':
/*     */               case '%':
/*     */               case '&':
/*     */               case '(':
/*     */               case ')':
/*     */               case '*':
/*     */               case '+':
/*     */               case ':':
/*     */               case '<':
/*     */               case '>':
/*     */               case '?':
/*     */               case '@':
/*     */               case '^':
/*     */               case '_':
/*     */               case '{':
/*     */               case '|':
/*     */               case '}':
/*     */               case '~':
/* 182 */                 shiftByte = 2;
/*     */               }
/*     */ 
/*     */ 
/* 186 */             file.add(Byte.valueOf(shiftByte));
/*     */           }
/* 188 */         } else if ((instruction[0].equals("CONTROL")) || 
/* 189 */           (instruction[0].equals("CTRL"))) {
/* 190 */           if ((instruction[1].equals("ESCAPE")) || 
/* 191 */             (instruction[1].equals("ESC")))
/* 192 */             file.add(Byte.valueOf((byte)41));
/* 193 */           else if ((instruction[1].equals("PAUSE")) || 
/* 194 */             (instruction[1].equals("BREAK")))
/* 195 */             file.add(Byte.valueOf((byte)72));
/* 196 */           else if (instruction.length != 1) {
/* 197 */             if (functionKeyCheck(instruction[1]))
/* 198 */               file.add(Byte.valueOf(functionKeyToByte(instruction[1])));
/*     */             else
/* 200 */               file.add(Byte.valueOf(charToByte(instruction[1].charAt(0))));
/*     */           }
/* 202 */           else file.add(Byte.valueOf((byte)0));
/* 203 */           file.add(Byte.valueOf((byte)1));
/* 204 */         } else if (instruction[0].equals("ALT")) {
/* 205 */           if (instruction.length != 1) {
/* 206 */             if ((instruction[1].equals("ESCAPE")) || 
/* 207 */               (instruction[1].equals("ESC")))
/* 208 */               file.add(Byte.valueOf((byte)41));
/* 209 */             else if (instruction[1].equals("SPACE"))
/* 210 */               file.add(Byte.valueOf((byte)44));
/* 211 */             else if (instruction[1].equals("TAB"))
/* 212 */               file.add(Byte.valueOf((byte)43));
/* 213 */             else if (instruction.length != 1) {
/* 214 */               if (functionKeyCheck(instruction[1]))
/* 215 */                 file.add(Byte.valueOf(functionKeyToByte(instruction[1])));
/*     */               else
/* 217 */                 file.add(Byte.valueOf(charToByte(instruction[1].charAt(0))));
/*     */             }
/* 219 */             else file.add(Byte.valueOf((byte)0)); 
/*     */           }
/*     */           else {
/* 221 */             file.add(Byte.valueOf((byte)0));
/*     */           }
/* 223 */           file.add(Byte.valueOf((byte)-30));
/*     */         }
/* 225 */         else if (instruction[0].equals("ENTER")) {
/* 226 */           file.add(Byte.valueOf((byte)40));
/* 227 */           file.add(Byte.valueOf((byte)0));
/* 228 */         } else if (instruction[0].equals("SHIFT")) {
/* 229 */           if (instruction.length != 1) {
/* 230 */             if (instruction[1].equals("HOME"))
/* 231 */               file.add(Byte.valueOf((byte)74));
/* 232 */             else if (instruction[1].equals("TAB"))
/* 233 */               file.add(Byte.valueOf((byte)43));
/* 234 */             else if ((instruction[1].equals("WINDOWS")) || 
/* 235 */               (instruction[1].equals("GUI")))
/* 236 */               file.add(Byte.valueOf((byte)-29));
/* 237 */             else if (instruction[1].equals("INSERT"))
/* 238 */               file.add(Byte.valueOf((byte)73));
/* 239 */             else if (instruction[1].equals("PAGEUP"))
/* 240 */               file.add(Byte.valueOf((byte)75));
/* 241 */             else if (instruction[1].equals("PAGEDOWN"))
/* 242 */               file.add(Byte.valueOf((byte)78));
/* 243 */             else if (instruction[1].equals("DELETE"))
/* 244 */               file.add(Byte.valueOf((byte)76));
/* 245 */             else if (instruction[1].equals("END"))
/* 246 */               file.add(Byte.valueOf((byte)77));
/* 247 */             else if (instruction[1].equals("UPARROW"))
/* 248 */               file.add(Byte.valueOf((byte)82));
/* 249 */             else if (instruction[1].equals("DOWNARROW"))
/* 250 */               file.add(Byte.valueOf((byte)81));
/* 251 */             else if (instruction[1].equals("LEFTARROW"))
/* 252 */               file.add(Byte.valueOf((byte)80));
/* 253 */             else if (instruction[1].equals("RIGHTARROW")) {
/* 254 */               file.add(Byte.valueOf((byte)79));
/*     */             }
/* 256 */             file.add(Byte.valueOf((byte)-31));
/*     */           } else {
/* 258 */             file.add(Byte.valueOf((byte)-31));
/* 259 */             file.add(Byte.valueOf((byte)0));
/*     */           }
/*     */         } else {
/* 261 */           if (instruction[0].equals("REM"))
/*     */             continue;
/* 263 */           if ((instruction[0].equals("MENU")) || 
/* 264 */             (instruction[0].equals("APP"))) {
/* 265 */             file.add(Byte.valueOf((byte)101));
/* 266 */             file.add(Byte.valueOf((byte)0));
/* 267 */           } else if (instruction[0].equals("TAB")) {
/* 268 */             file.add(Byte.valueOf((byte)43));
/* 269 */             file.add(Byte.valueOf((byte)0));
/* 270 */           } else if (instruction[0].equals("SPACE")) {
/* 271 */             file.add(Byte.valueOf((byte)44));
/* 272 */             file.add(Byte.valueOf((byte)0));
/* 273 */           } else if ((instruction[0].equals("WINDOWS")) || 
/* 274 */             (instruction[0].equals("GUI"))) {
/* 275 */             if (instruction.length == 1) {
/* 276 */               file.add(Byte.valueOf((byte)-29));
/* 277 */               file.add(Byte.valueOf((byte)0));
/*     */             } else {
/* 279 */               file.add(Byte.valueOf(charToByte(instruction[1].charAt(0))));
/* 280 */               file.add(Byte.valueOf((byte)8));
/*     */             }
/* 282 */           } else if (instruction[0].equals("SYSTEMPOWER")) {
/* 283 */             file.add(Byte.valueOf((byte)-127));
/* 284 */             file.add(Byte.valueOf((byte)0));
/* 285 */           } else if (instruction[0].equals("SYSTEMSLEEP")) {
/* 286 */             file.add(Byte.valueOf((byte)-126));
/* 287 */             file.add(Byte.valueOf((byte)0));
/* 288 */           } else if (instruction[0].equals("SYSTEMWAKE")) {
/* 289 */             file.add(Byte.valueOf((byte)-125));
/* 290 */             file.add(Byte.valueOf((byte)0));
/* 291 */           } else if ((instruction[0].equals("ESCAPE")) || 
/* 292 */             (instruction[0].equals("ESC"))) {
/* 293 */             file.add(Byte.valueOf((byte)41));
/* 294 */             file.add(Byte.valueOf((byte)0));
/* 295 */           } else if (instruction[0].equals("CAPSLOCK")) {
/* 296 */             file.add(Byte.valueOf((byte)57));
/* 297 */             file.add(Byte.valueOf((byte)0));
/* 298 */           } else if (instruction[0].equals("PRINTSCREEN")) {
/* 299 */             file.add(Byte.valueOf((byte)70));
/* 300 */             file.add(Byte.valueOf((byte)0));
/* 301 */           } else if (instruction[0].equals("SCROLLLOCK")) {
/* 302 */             file.add(Byte.valueOf((byte)71));
/* 303 */             file.add(Byte.valueOf((byte)0));
/* 304 */           } else if ((instruction[0].equals("BREAK")) || 
/* 305 */             (instruction[0].equals("PAUSE"))) {
/* 306 */             file.add(Byte.valueOf((byte)72));
/* 307 */             file.add(Byte.valueOf((byte)0));
/* 308 */           } else if (instruction[0].equals("INSERT")) {
/* 309 */             file.add(Byte.valueOf((byte)73));
/* 310 */             file.add(Byte.valueOf((byte)0));
/* 311 */           } else if (instruction[0].equals("HOME")) {
/* 312 */             file.add(Byte.valueOf((byte)74));
/* 313 */             file.add(Byte.valueOf((byte)0));
/* 314 */           } else if (instruction[0].equals("END")) {
/* 315 */             file.add(Byte.valueOf((byte)77));
/* 316 */             file.add(Byte.valueOf((byte)0));
/* 317 */           } else if (instruction[0].equals("PAGEUP")) {
/* 318 */             file.add(Byte.valueOf((byte)75));
/* 319 */             file.add(Byte.valueOf((byte)0));
/* 320 */           } else if (instruction[0].equals("DELETE")) {
/* 321 */             file.add(Byte.valueOf((byte)76));
/* 322 */             file.add(Byte.valueOf((byte)0));
/* 323 */           } else if (instruction[0].equals("PAGEDOWN")) {
/* 324 */             file.add(Byte.valueOf((byte)78));
/* 325 */             file.add(Byte.valueOf((byte)0));
/* 326 */           } else if ((instruction[0].equals("RIGHTARROW")) || 
/* 327 */             (instruction[0].equals("RIGHT"))) {
/* 328 */             file.add(Byte.valueOf((byte)79));
/* 329 */             file.add(Byte.valueOf((byte)0));
/* 330 */           } else if ((instruction[0].equals("LEFTARROW")) || 
/* 331 */             (instruction[0].equals("LEFT"))) {
/* 332 */             file.add(Byte.valueOf((byte)80));
/* 333 */             file.add(Byte.valueOf((byte)0));
/* 334 */           } else if ((instruction[0].equals("DOWNARROW")) || 
/* 335 */             (instruction[0].equals("DOWN"))) {
/* 336 */             file.add(Byte.valueOf((byte)81));
/* 337 */             file.add(Byte.valueOf((byte)0));
/* 338 */           } else if ((instruction[0].equals("UPARROW")) || 
/* 339 */             (instruction[0].equals("UP"))) {
/* 340 */             file.add(Byte.valueOf((byte)82));
/* 341 */             file.add(Byte.valueOf((byte)0));
/* 342 */           } else if (instruction[0].equals("NUMLOCK")) {
/* 343 */             file.add(Byte.valueOf((byte)83));
/* 344 */             file.add(Byte.valueOf((byte)0));
/* 345 */           } else if (instruction[0].equals("STOP")) {
/* 346 */             file.add(Byte.valueOf((byte)-75));
/* 347 */             file.add(Byte.valueOf((byte)0));
/* 348 */           } else if ((instruction[0].equals("PLAY")) || 
/* 349 */             (instruction[0].equals("PAUSE"))) {
/* 350 */             file.add(Byte.valueOf((byte)-51));
/* 351 */             file.add(Byte.valueOf((byte)0));
/* 352 */           } else if (instruction[0].equals("MUTE")) {
/* 353 */             file.add(Byte.valueOf((byte)-30));
/* 354 */             file.add(Byte.valueOf((byte)0));
/* 355 */           } else if (instruction[0].equals("VOLUMEUP")) {
/* 356 */             file.add(Byte.valueOf((byte)-23));
/* 357 */             file.add(Byte.valueOf((byte)0));
/* 358 */           } else if (instruction[0].equals("VOLUMEDOWN")) {
/* 359 */             file.add(Byte.valueOf((byte)-22));
/* 360 */             file.add(Byte.valueOf((byte)0));
/* 361 */           } else if (functionKeyCheck(instruction[0]))
/*     */           {
/* 363 */             file.add(Byte.valueOf(functionKeyToByte(instruction[0])));
/* 364 */             file.add(Byte.valueOf((byte)0));
/*     */           }
/*     */         }
/*     */ 
/* 368 */         if (((delayOverride ? 0 : 1) & (defaultDelay != 0 ? 1 : 0)) != 0)
/* 369 */           while (defaultDelay > 0) {
/* 370 */             file.add(Byte.valueOf((byte)0));
/* 371 */             if (defaultDelay > 255) {
/* 372 */               file.add(Byte.valueOf((byte)-1));
/* 373 */               defaultDelay -= 255;
/*     */             } else {
/* 375 */               file.add(Byte.valueOf((byte)defaultDelay));
/* 376 */               defaultDelay = 0;
/*     */             }
/*     */           }
/*     */       }
/*     */       catch (Exception e) {
/* 381 */         System.out.println("Error on Line: " + (i + 1));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 387 */     byte[] data = new byte[file.size()];
/* 388 */     for (int i = 0; i < file.size(); i++)
/* 389 */       data[i] = ((Byte)file.get(i)).byteValue();
/*     */     try
/*     */     {
/* 392 */       File someFile = new File(fileDest);
/* 393 */       FileOutputStream fos = new FileOutputStream(someFile);
/* 394 */       fos.write(data);
/* 395 */       fos.flush();
/* 396 */       fos.close();
/*     */     } catch (Exception e) {
/* 398 */       System.out.print("Failed to write hex file!");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static byte charToByte(char c)
/*     */   {
/* 404 */     if ((c >= 'a') && (c <= 'z'))
/*     */     {
/* 406 */       return (byte)(c - ']');
/* 407 */     }if ((c >= 'A') && (c <= 'Z'))
/*     */     {
/* 409 */       return (byte)(c - '=');
/* 410 */     }if ((c >= '1') && (c <= '9'))
/*     */     {
/* 412 */       return (byte)(c - '\023');
/*     */     }
/* 414 */     switch (c) {
/*     */     case ' ':
/* 416 */       return 44;
/*     */     case '!':
/* 418 */       return 30;
/*     */     case '@':
/* 420 */       return 31;
/*     */     case '#':
/* 422 */       return 32;
/*     */     case '$':
/* 424 */       return 33;
/*     */     case '%':
/* 426 */       return 34;
/*     */     case '^':
/* 428 */       return 35;
/*     */     case '&':
/* 430 */       return 36;
/*     */     case '*':
/* 432 */       return 37;
/*     */     case '(':
/* 434 */       return 38;
/*     */     case ')':
/*     */     case '0':
/* 437 */       return 39;
/*     */     case '-':
/*     */     case '_':
/* 440 */       return 45;
/*     */     case '+':
/*     */     case '=':
/* 443 */       return 46;
/*     */     case '[':
/*     */     case '{':
/* 446 */       return 47;
/*     */     case ']':
/*     */     case '}':
/* 449 */       return 48;
/*     */     case '\\':
/*     */     case '|':
/* 452 */       return 49;
/*     */     case ':':
/*     */     case ';':
/* 455 */       return 51;
/*     */     case '"':
/*     */     case '\'':
/* 458 */       return 52;
/*     */     case '`':
/*     */     case '~':
/* 461 */       return 53;
/*     */     case ',':
/*     */     case '<':
/* 464 */       return 54;
/*     */     case '.':
/*     */     case '>':
/* 467 */       return 55;
/*     */     case '/':
/*     */     case '?':
/* 470 */       return 56;
/*     */     }
/*     */ 
/* 473 */     return -103;
/*     */   }
/*     */ 
/*     */   private static boolean functionKeyCheck(String possibleFKey)
/*     */   {
/* 483 */     return (possibleFKey.equals("F1")) || (possibleFKey.equals("F2")) || 
/* 478 */       (possibleFKey.equals("F3")) || (possibleFKey.equals("F4")) || 
/* 479 */       (possibleFKey.equals("F5")) || (possibleFKey.equals("F6")) || 
/* 480 */       (possibleFKey.equals("F7")) || (possibleFKey.equals("F8")) || 
/* 481 */       (possibleFKey.equals("F9")) || (possibleFKey.equals("F10")) || 
/* 482 */       (possibleFKey.equals("F11")) || (possibleFKey.equals("F12"));
/*     */   }
/*     */ 
/*     */   private static byte functionKeyToByte(String fKey)
/*     */   {
/* 489 */     if (fKey.equals("F1"))
/* 490 */       return 58;
/* 491 */     if (fKey.equals("F2"))
/* 492 */       return 59;
/* 493 */     if (fKey.equals("F3"))
/* 494 */       return 60;
/* 495 */     if (fKey.equals("F4"))
/* 496 */       return 61;
/* 497 */     if (fKey.equals("F5"))
/* 498 */       return 62;
/* 499 */     if (fKey.equals("F6"))
/* 500 */       return 63;
/* 501 */     if (fKey.equals("F7"))
/* 502 */       return 64;
/* 503 */     if (fKey.equals("F8"))
/* 504 */       return 65;
/* 505 */     if (fKey.equals("F9"))
/* 506 */       return 66;
/* 507 */     if (fKey.equals("F10"))
/* 508 */       return 67;
/* 509 */     if (fKey.equals("F11"))
/* 510 */       return 68;
/* 511 */     if (fKey.equals("F12")) {
/* 512 */       return 69;
/*     */     }
/* 514 */     return -103;
/*     */   }
/*     */ }

/* Location:           /Users/joe/Dropbox/Tiny Dragon Apps/RubberDucky/Original Files/duckencode.jar
 * Qualified Name:     Encoder
 * JD-Core Version:    0.6.0
 */