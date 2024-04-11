<?php

namespace castialnet\rank;

abstract class Rank
{
	const RANK_NONE 		 	= 0x00;
	const RANK_REGULAR_MEMBER 	= 0x01;

	const RANK_STAFF 		 	= 0x10;
	const RANK_STAFF_MODERATOR 	= 0x11;
	const RANK_STAFF_OPERATOR 	= 0x12;

	const RANK_PAID 	 		= 0x20;
	const RANK_PAID_VIP 		= 0x21;
	const RANK_PAID_VIP_PLUS 	= 0x22;

	const RANK_FORMATS = [
		self::RANK_REGULAR_MEMBER => "",
		self::RANK_STAFF_MODERATOR => "&i&emoderator&r",
		self::RANK_STAFF_OPERATOR => "&badmin"
	];

	const RANK_NAMES = [
		self::RANK_REGULAR_MEMBER 	=> "member",
		self::RANK_STAFF 			=> "staff",
		self::RANK_STAFF_MODERATOR 	=> "moderator",
		self::RANK_STAFF_OPERATOR 	=> "admin"
	];

	const PERMS = [
		self::RANK_REGULAR_MEMBER => [
			"castial.member.perm1"
		],
		self::RANK_STAFF => [
			"castial.staff.perm1"
		],
		self::RANK_STAFF_MODERATOR => [
			"castial.mod.perm1"
		],
		self::RANK_STAFF_OPERATOR => [
			"castial.op.perm1",
			"castial.op.perm2"
		]
	];

	public static function getAllPerms(int $rank, bool $inherit = true)
	{
		$isStaff = ($rank & 0xf0) === self::RANK_STAFF;
		if($inherit) {
			$perms = [];
			foreach (self::PERMS as $key => $permsList) {
				if($key <= $rank) {
					foreach ($permsList as $perm) {
						$perms[] = $perm;
					}
				}
			}
			return $perms;
		} else {
			return self::PERMS[$rank];
		}
	}

	public static function getRankByName(string $rank)
	{
		$flip = array_flip(self::RANK_NAMES);
		return isset($flip[$rank]) ? $flip[$rank] : false;
	}

	public static function getRankName(int $rank)
	{
		return isset(self::RANK_NAMES[$rank]) ? self::RANK_NAMES[$rank] : false;
	}
}