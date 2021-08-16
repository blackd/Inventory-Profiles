parser grammar ProfilesParser;

options {
    tokenVocab=ProfilesLexer;
}

script
	:	profile* EOF
	;

profile : 'profile' Id ('activate' activeSlotName)? slotsDef;
slotsDef : (slotDef)* ;
slotDef : slotname  (itemDef)*? ;
itemDef : itemName ('->' (enchantments | potion))?;
itemName : NamespacedId;
enchantments : ENCHANTMENTS '[' enchantment (',' enchantment)* ']';
potion : POTION enchantment;
enchantment : '{' name (',' level)? '}';
level: 'lvl' ':' Level ;
name: 'id' ':' NamespacedId;

slotname : (HOT1|HOT2|HOT3|HOT4|HOT5|HOT6|HOT7|HOT8|HOT9|CHESTPLATE|LEGS|FEET|HEAD|OFFHAND);
activeSlotName : (HOT1|HOT2|HOT3|HOT4|HOT5|HOT6|HOT7|HOT8|HOT9);