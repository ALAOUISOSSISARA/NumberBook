<?php
header("Content-Type: application/json");
require_once __DIR__ . '/../repository/PhoneBookRepository.php';

if (empty($_GET['keyword'])) {
    echo json_encode([]);
    exit;
}

$repo    = new PhoneBookRepository();
$matches = $repo->findByKeyword(trim($_GET['keyword']));

echo json_encode($matches);
?>