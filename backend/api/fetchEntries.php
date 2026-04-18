<?php
header("Content-Type: application/json");
require_once __DIR__ . '/../repository/PhoneBookRepository.php';

$repo    = new PhoneBookRepository();
$entries = $repo->fetchAll();

echo json_encode($entries);
?>